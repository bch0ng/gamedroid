package edu.us.ischool.bchong.info448project

import android.app.Service
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.Sensor.TYPE_ACCELEROMETER
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Vibrator
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.getSystemService
import android.util.Log
import android.view.View
import java.util.*
import kotlin.concurrent.schedule

//TODO Everything
class RollTheDice : NetworkGame {
    enum class gameStates() {
            PREGAME(), INGAME(), POSTGAME()
    }
    private var pregameDuration=R.integer.dice_pregame_duration.toLong()
    private var gameDuration=R.integer.dice_game_duration.toLong()
    private var postgameDuration=R.integer.dice_postgame_duration.toLong()

    var gameState=gameStates.PREGAME
    override var gameFragment: GameFragment? = null
    private lateinit var frag: DiceFragment
    private var accumulatedRollEnergy:Double=0.0

    var score: Double = 0.0
    var prevAccelerations= arrayOf(0f,0f,0f)
    val OPPONENT_SHAKE="shake"
    lateinit var vibrator: Vibrator
    val vibrationStrength=(R.integer.dice_vibration_strength).toLong()

    override fun newMessage(message: Bundle) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        if(message.get("type")==OPPONENT_SHAKE){
            vibrate(message.get("strength") as Double)
        }
    }

    override fun sendMessage(message: Bundle) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun vibrate(strength:Double){
        vibrator.vibrate(vibrationStrength)
        frag.opponentRolled(strength)
    }
    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    constructor() {
        gameFragment = FlipFragment().newInstance(this)
        this.frag=gameFragment as DiceFragment
    }
    //When the player attempts to roll before the game starts
    private fun pregameRoll(){

    }
    //When the player attempts to roll
    private fun inGameRoll(event: SensorEvent){
        frag.diceRoll(event.values[0],event.values[1],accumulatedRollEnergy)
        sendOpponentRollData(accumulatedRollEnergy*(Random().nextDouble()+0.5))
    }
    private fun sendOpponentRollData(strength:Double){
        var message:Bundle= Bundle.EMPTY
        message.putString("type",OPPONENT_SHAKE)
        message.putDouble("strength",strength)
        sendMessage(message)
    }
    //When the player attempts to roll after the game is completed
    private fun postGameRoll(){

    }
    //Was the dice rolled? based on an acceleration change
    private fun isRoll(values:FloatArray):Boolean{
        values[0]+values[1]+values[2]>.5
        return true
    }
    private fun increaseAccumulatedEnergy(event:SensorEvent):Boolean{
        val eventValues=event.values
        var roll=isRoll(eventValues)
        if(roll){
            accumulatedRollEnergy+=0.4*(eventValues[0]+eventValues[1]+eventValues[2])
        }
        return roll
    }
    private fun accelerationEvent(event: SensorEvent?){
        if(increaseAccumulatedEnergy(event!!)){
            when(gameState){
                gameStates.PREGAME ->pregameRoll()
                gameStates.INGAME->inGameRoll(event)
                gameStates.POSTGAME->postGameRoll()

            }
        }
    }


    override fun onSensorChanged(event: SensorEvent?) {
        //Log.v("test", "sensorchanged")
        val type = event!!.sensor.type
        if (type == Sensor.TYPE_ACCELEROMETER) {
            accelerationEvent(event)
        }
    }

    override fun OnRegisterMotionListener() {

    }


    override fun OnStart() {
        score = 0.0
        vibrator = frag.context!!.getSystemService(VIBRATOR_SERVICE) as Vibrator
        //This timer is for the games pregame
        Timer().schedule(object : TimerTask() {
            override fun run() {
                endPregame()
            }
        }, pregameDuration)
        Log.v("test", "Dice Listener Started")
    }
    private fun endPregame(){
        gameState=gameStates.INGAME
        Timer("startInGame", false).schedule(gameDuration) {
            gameState=gameStates.POSTGAME
        }
        startPostgame()
    }
    private fun startPostgame(){
        frag.displayRestart(score.toInt(),0,false)
    }

    override fun OnEnd(): Int {
        return score.toInt()
    }

}