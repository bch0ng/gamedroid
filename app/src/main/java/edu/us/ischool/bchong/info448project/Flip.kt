package edu.us.ischool.bchong.info448project

import android.app.Service
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.Sensor.TYPE_ACCELEROMETER
import android.util.Log


class Flip:Game,MotionListener {
    var score:Double=0.0
    var mGZ:Float=0.0f
    var MAX_COUNT_GZ_CHANGE=0
    var mEventCountSinceGZChanged=0
    override fun everyFrame(event:SensorEvent?) {
        val type = event!!.sensor.type
        if (type == Sensor.TYPE_ACCELEROMETER) {
            val gz = event.values[2]
            if (mGZ === 0.0f) {
                mGZ = gz
            } else {
                if (mGZ * gz < 0) {
                    mEventCountSinceGZChanged++
                    if (mEventCountSinceGZChanged === MAX_COUNT_GZ_CHANGE) {
                        mGZ = gz
                        mEventCountSinceGZChanged = 0
                        OnFlip()
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

    override fun OnFlip() {
        score++
        Log.i("TEST", "$score")
    }
    override fun OnStart() {
        score=0.0
        // activityContext.setLayout()
    }

    override fun OnEnd(): Int {
        return score.toInt()
    }

}