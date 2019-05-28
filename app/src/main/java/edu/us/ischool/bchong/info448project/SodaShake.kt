package edu.us.ischool.bchong.info448project

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.util.Log

class SodaShake : Game {
    override var gameFragment: GameFragment? = null
    private var shakeCapacity: Int = 0


    constructor() {
        gameFragment = SodaShakeFragment().newInstance(this)

    }


    override fun OnStart() {
        shakeCapacity = (20..100).random()
        Log.i("TEST", "Shake capacity: $shakeCapacity")
    }

    override fun OnEnd() : Int {
        return 0
    }

    override fun OnRegisterMotionListener() {

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {

    }

}
