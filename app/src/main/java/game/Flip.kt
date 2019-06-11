package game

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.util.Log


class Flip : Game {

    override fun onPause() {
        //
    }

    override fun onFragmentStart() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStart(name:String) {
        score = 0.0
        this.name=name

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

    //On initialization make the fragment
    constructor() {
        gameFragment = FlipFragment().newInstance(this)
        this.frag = gameFragment as FlipFragment
    }

    //When the phone is accelerated add score
    private fun accelerationEvent(event: SensorEvent?) {
        //incrementAccByMoment(event)
        incrementAccByPeriod(event)
        //OnFlip()
        if(event!!.values[1]>1||event.values[2]>1){
            frag.rotateFlipAccent(1)
        }
    }

    //Alternate scoring method for accelerations
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

    //Alternate scoring method for accelerations
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

    //On gyro event increment score
    private fun gyroEvent(event: SensorEvent) {
        Log.i("test", "${event.values[0]}")
        val values = event.values
        incrementGyroByMoment(values)
    }

    //Gyro scoring formula
    private fun incrementGyroByMoment(values:FloatArray){
        if (values[0] + values[1] + values[2] > 0.4) {
            val scoreUp=(Math.pow((previousRotations[0] - values[0]).toDouble(), 2.0) +
                    Math.pow((previousRotations[1] - values[1]).toDouble(), 2.0) +
                    Math.pow((previousRotations[2] - values[2]).toDouble(), 2.0)) * 0.095
            score += scoreUp
            previousRotations[0] = values[0]
            previousRotations[1] = values[1]
            previousRotations[2] = values[2]
            this.rotationAccumulation=rotationAccumulation*.95f+scoreUp.toFloat()*5
            OnFlip()
            frag.rotateFlipAccent(rotationAccumulation.toInt())
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

    //Tells the fragment to update visuals
    private fun OnFlip() {
        //score++
        frag.OnFlip(score.toInt())
        Log.v("test", "$score")
    }

    //Returns the score
    override fun onEnd(): Int {
        return score.toInt()
    }

}