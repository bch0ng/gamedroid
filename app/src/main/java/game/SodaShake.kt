package game

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.util.Log
import kotlin.math.sqrt
import android.os.Vibrator
import edu.us.ischool.bchong.info448project.R
import java.util.*
import android.support.v4.content.ContextCompat.getSystemService



class SodaShake : Game {

    lateinit var name:String
    override var gameFragment: GameFragment? = null
    private var shakeCapacity: Int = 0
    private var numOfShakes: Int = 0


    private val SHAKE_THRESHOLD_GRAVITY = 2.7f
    private val SHAKE_SLOP_TIME_MS = 500
    private val SHAKE_COUNT_RESET_TIME_MS = 3000

    private var mListener: onShakeListener? = null
    private var mShakeTimestamp: Long = 0
    private var mShakeCount: Int = 0

    private var mSensorManager: SensorManager? = null
    private var mAccelerometer: Sensor? = null
    private var mShakeDetector: SodaShake? = null
    private var context: Context? = null

    private lateinit var vibrator:Vibrator
    var timer: Timer? = null

    interface onShakeListener {
        fun onShake(count: Int)
    }

    fun setOnShakeListener(listener: onShakeListener) {
        this.mListener = listener
    }


    constructor(context: Context) {
        this.context = context
        gameFragment = SodaShakeFragment().newInstance(this)
    }


    override fun onStart(name:String) {
        this.name=name
        vibrator=context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        mSensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mShakeDetector = SodaShake(context as Context)
        mSensorManager?.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        mShakeDetector?.setOnShakeListener(object : onShakeListener {
            override fun onShake(count: Int) {
                Log.i("TEST", "Num of shakes: $count / $shakeCapacity")
                vibrator.vibrate(count.toLong()/2)
                if (count >= shakeCapacity) {
                    explodeSoda()
                }
            }
        })

        shakeCapacity = (50..250).random()
        numOfShakes = 0
        Log.i("TEST", "Shake capacity: $shakeCapacity")
    }

    fun explodeSoda() {
        (gameFragment as SodaShakeFragment).endGame()
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?

        @Suppress("DEPRECATION")
        vibrator?.vibrate(2000)
        onEnd()
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

    override fun onEnd() : Int {
        mSensorManager?.unregisterListener(mShakeDetector)
        val audioPlayer = MediaPlayer.create(context, R.raw.popping_sound)
        audioPlayer.start()
        audioPlayer.setOnCompletionListener {
            it.stop()
        }
        return 0
    }

    override fun onRegisterMotionListener() {}

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

    override fun onFragmentStart() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPause() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (mListener != null) {
            var x: Float = event!!.values[0]
            var y: Float = event!!.values[1]
            var z: Float = event!!.values[2]

            var gX: Float = x / SensorManager.GRAVITY_EARTH
            var gY: Float = y / SensorManager.GRAVITY_EARTH
            var gZ: Float = z / SensorManager.GRAVITY_EARTH

            // gForce will be close to 1 when there is no movement.
            var gForce: Float = sqrt(gX * gX + gY * gY + gZ * gZ)

            if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                val now = System.currentTimeMillis()
                // ignore shake events too close to each other (500ms)
                /*if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                    return
                }*/

                mShakeTimestamp = now
                mShakeCount++

                mListener?.onShake(mShakeCount)
            }
        }
    }
}
