package edu.us.ischool.bchong.info448project

import android.hardware.Sensor
import android.hardware.SensorEvent

class Telephone: Game {
    override var gameFragment: GameFragment? = null

    constructor() {
        gameFragment = TelephoneFragment().newInstance(this)
    }

    override fun OnStart() {

    }

    override fun OnRegisterMotionListener() {

    }

    override fun OnEnd(): Int {
        return 0
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSensorChanged(event: SensorEvent?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}