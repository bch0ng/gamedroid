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
import java.time.LocalTime


class Flip : Game {
    override fun onPause() {
        //
    }

    override fun onFragmentStart() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStart(name: String) {
        this.name = name
        score = 0.0
        Log.v("test", "Listener Started")
        //this.linearAccelerometer = GameApp.applicationContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    lateinit var name: String
    override var gameFragment: GameFragment? = null
    private lateinit var frag: FlipFragment

    var score: Double = 0.0
    var mGZ: Float = 0.0f
    var MAX_COUNT_GZ_CHANGE = 0
    var mEventCountSinceGZChanged = 0
    var lastGZ = 0f
    var rotationAccumulation = 0f
    var previousAcc:FloatArray = FloatArray(3)
    var previousRotations = arrayOf(0f, 0f, 0f)

    var accTimeStarted=false
    var accStartingVal=0f
    var accStartTime:Long=0

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    constructor() {
        gameFragment = FlipFragment().newInstance(this)
        this.frag = gameFragment as FlipFragment
    }

    private fun accelerationEvent(event: SensorEvent?) {
        //incrementAccByMoment(event)
        incrementAccByPeriod(event)
        OnFlip()
    }
    private fun incrementAccByPeriod(event:SensorEvent?){
        val values=event!!.values
        var valueSum=Math.abs(values[0]*previousAcc[0])+Math.abs(values[1]*previousAcc[1])
        +Math.abs(values[2]*previousAcc[2])
        //Log.v("flip","valueSum: $valueSum")
        if(valueSum>2200.7&&!accTimeStarted){
            if(accTimeStarted) {
               // Log.v("flip","Acc ended $valueSum")
                //accTimeStarted = false
                //score+=(System.currentTimeMillis()-accStartTime)*.00001
            }else{
                Log.v("flip","Acc started $valueSum")
                accTimeStarted = true
                accStartingVal = valueSum
                accStartTime=System.currentTimeMillis()
            }
        }else if(accTimeStarted){
            score+=1
        }
        if(valueSum<400){
            accTimeStarted=false
        }
        /*else if(accTimeStarted&&valueSum>800.1){
            Log.v("flip","Minor deacc detected started $valueSum")
            accTimeStarted=false
            score+=(System.currentTimeMillis()-accStartTime)
        }*/
        previousAcc=event.values
    }

    private fun incrementAccByMoment(event: SensorEvent?){
        val gz = event!!.values[2]
        //Log.v("test", "$gz")
        if (mGZ === 0.0f) {
            mGZ = gz
        } else {
            var delta = gz - lastGZ
            if (gz * mGZ > 2.4) {
                score += Math.pow(gz * (mGZ) * 0.003, 2.0)
                +Math.pow(event.values[1] * 0.003, 2.0)
                +Math.pow(event.values[0] * 0.003, 2.0)
            }
        }
    }

    private fun gyroEvent(event: SensorEvent) {
        Log.i("test", "${event.values[0]}")
        val values = event.values
        //incrementGyroByMoment(values)
    }
    private fun incrementGyroByMoment(values:FloatArray){
        if (values[0] + values[1] + values[2] > 0.4) {
            score += (Math.pow((previousRotations[0] - values[0]).toDouble(), 2.0) +
                    Math.pow((previousRotations[1] - values[1]).toDouble(), 2.0) +
                    Math.pow((previousRotations[2] - values[2]).toDouble(), 2.0)) * 0.095
            previousRotations[0] = values[0]
            previousRotations[1] = values[1]
            previousRotations[2] = values[2]
            OnFlip()
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        //Log.v("test", "sensorchanged")
        val type = event!!.sensor.type
        if (type == Sensor.TYPE_ACCELEROMETER) {
            accelerationEvent(event)
        } else if (type == Sensor.TYPE_GYROSCOPE) {
            gyroEvent(event)
        }
    }

    override fun onRegisterMotionListener() {

    }

    private fun OnFlip() {
        //score++
        frag.OnFlip(score.toInt())
        Log.v("test", "$score")
    }

    override fun onEnd(): Int {
        return score.toInt()
    }

}