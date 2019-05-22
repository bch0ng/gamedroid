package edu.us.ischool.bchong.info448project

import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.os.IBinder

class MotionSensorController(var motionListener: MotionListener): Service(),SensorEventListener {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    override fun onSensorChanged(p0: SensorEvent?) {
        motionListener.everyFrame(p0)
    }

    override fun equals(other: Any?): Boolean {
        return other==this
    }
}