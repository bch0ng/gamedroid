package edu.us.ischool.bchong.info448project

import game.Game
import game.GameFragment
import android.app.Activity
import android.app.Service
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.content.Intent
import android.hardware.SensorManager
import android.os.IBinder
import android.util.Log
import java.util.*
import android.media.MediaPlayer



class Telephone: Game, Service {
    override var gameFragment: GameFragment? = null
    var context: Context? = null
    var timer: Timer? = null
    var audioPlayer: MediaPlayer? = null
    var didGameStart = false
    var timeDelay: Long = 5000
    var didPlayerWin: Boolean = false
    var didCountHost: Boolean = false


    lateinit var name:String
    private var mGZ = 0f//gravity acceleration along the z axis
    private var mEventCountSinceGZChanged = 0
    private val MAX_COUNT_GZ_CHANGE = 10
    private var mSensorManager: SensorManager? = null


    constructor(context: Context) {
        this.context = context
        gameFragment = TelephoneFragment().newInstance(this)
    }

    override fun onStart(name:String) {
        this.name = name

        if (NearbyConnection.instance.isHosting()) {
            timeDelay = (5000..15000).random().toLong()
            Log.i("TEST", "sending message from telephone: $timeDelay")
            NearbyConnection.instance.sendMessageAll("telephone_time: $timeDelay")
        }

        mSensorManager = context?.getSystemService(SENSOR_SERVICE) as SensorManager

        mSensorManager?.registerListener(this,
            mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onRegisterMotionListener() {}

    override fun onEnd(): Int {
        mSensorManager?.unregisterListener(this)
        return 0
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onFragmentStart() {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun onPause() {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    fun setTime(delay: Long) {
        timeDelay = delay
        Log.i("TEST", "timeDelay in setTime: $timeDelay")
    }

    fun updatePlayerWin() {
        didPlayerWin = true
    }

    fun startGame() {
        if (didGameStart == false) {
            audioPlayer = MediaPlayer.create(context, R.raw.telephone_ring)
            audioPlayer?.setOnPreparedListener {
                timer = Timer("Timer")

                var timerTask = Task(context!!, timer!!, audioPlayer!!)
                Log.i("TEST", "Time: $timeDelay")

                timer?.schedule(timerTask, timeDelay, timeDelay)
            }
            didGameStart = true
        }
    }

    fun trackFlipDowns() {
        if (NearbyConnection.instance.isHosting() && !didCountHost) {
            NearbyConnection.instance.flipDownCount++
            Log.i("TEST", "track flipDowns")
            didCountHost = true
        }
        if (!NearbyConnection.instance.isHosting()) {
            NearbyConnection.instance.sendMessageAll("telephone:flippedDown")
        }
        Log.i("TEST", "flip down count ${NearbyConnection.instance.flipDownCount}")
        Log.i("TEST", "curr players: ${NearbyConnection.instance.getCurrPlayers().size}")
        if (NearbyConnection.instance.flipDownCount == NearbyConnection.instance.getCurrPlayers().size) {
            NearbyConnection.instance.sendMessageAll("telephone: start")
            startGame()
        }
    }


    override fun onSensorChanged(event: SensorEvent?) {
        val type = event?.sensor?.type
        if (type == Sensor.TYPE_ACCELEROMETER) {
            val gz = event.values[2]
            if (mGZ === 0f) {
                mGZ = gz
            } else {
                if (mGZ * gz < 0f) {
                    mEventCountSinceGZChanged++
                    if (mEventCountSinceGZChanged === MAX_COUNT_GZ_CHANGE) {
                        mGZ = gz
                        mEventCountSinceGZChanged = 0
                        if (gz > 0) {
                            Log.i("TEST", "now screen is facing up.")
                            if (didGameStart == true) {
                                audioPlayer?.stop()
                                // TODO: Show win or lose text depending on who wins or loses
                                if (!didPlayerWin) {
                                    (gameFragment as TelephoneFragment).showWinText()
                                     NearbyConnection.instance.sendMessageAll("telephone_win")
                                } else {
                                    (gameFragment as TelephoneFragment).showLoseText()
                                }
                                onEnd()
                            }

                        } else if (gz < 0) {
                            Log.i("TEST", "now screen is facing down.")
                            trackFlipDowns()
                        }
                    }
                } else {
                    if (mEventCountSinceGZChanged > 0) {
                        mGZ = gz
                        mEventCountSinceGZChanged = 0
                    }
                }
            }
        }
    }

    private class Task(val context: Context, val timer: Timer, val audioPlayer: MediaPlayer): TimerTask() {

        override fun run() {
            (context as Activity).runOnUiThread(object: Runnable {
                override fun run() {
                    audioPlayer.start()
                    Log.i("TEST", "Timer ended")
                    timer.cancel()
                    timer.purge()
                }
            })
        }
    }
}