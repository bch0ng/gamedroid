package edu.us.ischool.bchong.info448project

import Game.Game
import Game.GameFragment
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

    private var mGZ = 0f//gravity acceleration along the z axis
    private var mEventCountSinceGZChanged = 0
    private val MAX_COUNT_GZ_CHANGE = 10
    private var mSensorManager: SensorManager? = null

    constructor(context: Context) {
        this.context = context
        gameFragment = TelephoneFragment().newInstance(this)
    }

    override fun onStart() {
        mSensorManager = context?.getSystemService(SENSOR_SERVICE) as SensorManager

        mSensorManager?.registerListener(this,
            mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_GAME)

        audioPlayer = MediaPlayer.create(context, R.raw.telephone_ring)
        audioPlayer?.setOnPreparedListener {
            timer = Timer("Timer")

            var delay: Long = (5000..15000).random().toLong()
            var timerTask = Task(context!!, timer!!, audioPlayer!!)
            Log.i("TEST", "Time: $delay")

            timer?.schedule(timerTask, delay, delay)
        }



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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPause() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
                            audioPlayer?.stop()


                            // TODO: Show win or lose text depending on who wins or loses
                            (gameFragment as TelephoneFragment).showWinText()
                        } else if (gz < 0) {
                            Log.i("TEST", "now screen is facing down.")
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
                    timer.cancel()
                    timer.purge()
                }
            })
        }
    }
}