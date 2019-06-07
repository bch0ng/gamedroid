package edu.us.ischool.bchong.info448project

import android.app.Service
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.Sensor.TYPE_ACCELEROMETER
import android.hardware.SensorManager
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.getSystemService
import android.util.Log
import android.view.View


class Flip : Game {

    override var gameFragment: GameFragment? = null
    private lateinit var frag: FlipFragment

    var score: Double = 0.0
    var mGZ: Float = 0.0f
    var MAX_COUNT_GZ_CHANGE = 0
    var mEventCountSinceGZChanged = 0
    var lastGZ=0f
    var rotationAccumulation=0f
    var previousRotations= arrayOf(0f,0f,0f)

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    constructor() {
        gameFragment = FlipFragment().newInstance(this)
        this.frag=gameFragment as FlipFragment
    }
    private fun accelerationEvent(event: SensorEvent?){
        val gz = event!!.values[2]
        //Log.v("test", "$gz")
        if (mGZ === 0.0f) {
            mGZ = gz
        }else{
            var delta=gz-lastGZ
            if(gz*mGZ>0){
                //Log.v("event","happened")
                score+=Math.pow(gz*(mGZ)*0.006,2.0)
            }
        }
    }
    private fun gyroEvent(event: SensorEvent){
        Log.i("test","${event.values[0]}")
        score+=(Math.pow((previousRotations[0]-event.values[0]).toDouble(),2.0)+
                Math.pow((previousRotations[1]-event.values[1]).toDouble(),2.0)+
                Math.pow((previousRotations[2]-event.values[2]).toDouble(),2.0))*0.0095
        previousRotations[0]=event.values[0]
        previousRotations[1]=event.values[1]
        previousRotations[2]=event.values[2]
        OnFlip()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        //Log.v("test", "sensorchanged")
        val type = event!!.sensor.type
        if (type == Sensor.TYPE_ACCELEROMETER) {
            accelerationEvent(event)
        }else if(type==Sensor.TYPE_GYROSCOPE){
            gyroEvent(event)
        }
    }

    override fun OnRegisterMotionListener() {

    }

    private fun OnFlip() {
        //score++
        frag.OnFlip(score.toInt())
        Log.v("test", "$score")
    }

    override fun OnStart() {
        score = 0.0

        Log.v("test", "Listener Started")
        //this.linearAccelerometer = GameApp.applicationContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun OnEnd(): Int {
        return score.toInt()
    }

}