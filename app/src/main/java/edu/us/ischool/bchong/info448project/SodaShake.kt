package edu.us.ischool.bchong.info448project

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.util.Log
import kotlin.math.sqrt
import android.os.Vibrator

class SodaShake : Game {
    override var gameFragment: GameFragment? = null
    private var shakeCapacity: Int = 0
    private var numOfShakes: Int = 0


    private val SHAKE_THRESHOLD_GRAVITY = 2.7f
    private val SHAKE_SLOP_TIME_MS = 500
    private val SHAKE_COUNT_RESET_TIME_MS = 3000

    private var mListener: OnShakeListener? = null
    private var mShakeTimestamp: Long = 0
    private var mShakeCount: Int = 0

    private var mSensorManager: SensorManager? = null
    private var mAccelerometer: Sensor? = null
    private var mShakeDetector: SodaShake? = null
    private var context: Context? = null


    interface OnShakeListener {
        fun onShake(count: Int)
    }

    fun setOnShakeListener(listener: OnShakeListener) {
        this.mListener = listener
    }


    constructor(context: Context) {
        this.context = context
        gameFragment = SodaShakeFragment().newInstance(this)
    }


    override fun OnStart() {
        mSensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mShakeDetector = SodaShake(context as Context)
        mSensorManager?.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        mShakeDetector?.setOnShakeListener(object : OnShakeListener {
            override fun onShake(count: Int) {
                Log.i("TEST", "Num of shakes: $count / $shakeCapacity")
                if (count >= shakeCapacity) {
                    explodeSoda()
                }
            }
        })

        shakeCapacity = (300..2000).random()
        numOfShakes = 0
        Log.i("TEST", "Shake capacity: $shakeCapacity")
    }

    fun explodeSoda() {
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
        @Suppress("DEPRECATION")
        vibrator?.vibrate(1500)
        (gameFragment as SodaShakeFragment).endGame()
        OnEnd()
    }

    override fun OnEnd() : Int {
        mSensorManager?.unregisterListener(mShakeDetector)
        return 0
    }

    override fun OnRegisterMotionListener() {}

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

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
