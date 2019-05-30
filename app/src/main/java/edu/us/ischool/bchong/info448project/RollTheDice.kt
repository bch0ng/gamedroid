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
    enum class messageStates(val code: String) {
        START_GAME("startGame"),
        OPPONENT_SHAKE("shake"),
        OPPONENT_SCORE("opponentScore"),
        GAME_OVER("gameOver")
    }
    private var pregameDuration=R.integer.dice_pregame_duration.toLong()
    var gameDuration=R.integer.dice_game_duration.toLong()
    private var postgameDuration=R.integer.dice_postgame_duration.toLong()

    var gameState=gameStates.PREGAME
    override var gameFragment: GameFragment? = null
    private var frag: DiceFragment
    private var accumulatedRollEnergy:Double=0.0

    var score: Double = 0.0
    var prevAccelerations= arrayOf(0f,0f,0f)
    lateinit var myId:String
    lateinit var players:Array<Pair<String,String>>       //ID and name


    lateinit var vibrator: Vibrator
    val vibrationStrength=(R.integer.dice_vibration_strength).toLong()

    //When the game gets the starting signal from the server
    private fun StartGame(message: Bundle){
        players=message.getSerializable("players") as Array<Pair<String,String>>
        myId=message.getString("playerId")
        val myName=message.getString("playerName") as String
        frag.StartGame(Pair(myId,myName),players)
    }
    override fun newMessage(message: Bundle) {
        val type=message.get("type")
        when(type){
            messageStates.START_GAME -> StartGame(message)
            messageStates.OPPONENT_SHAKE -> vibrate(message.get("id") as String,message.get("strength") as Double)
            messageStates.OPPONENT_SCORE -> newOpponentScore(message.get("id") as String,message.get("score") as Int)
            messageStates.GAME_OVER -> gameOver(message.get("scores") as Array<Pair<String,Int>>)
            else -> Log.e("dice","Invalid message $type")
        }
    }
    private fun gameOver(playerScores:Array<Pair<String,Int>>){
        var highestScore=Pair<String,Int>(myId,score.toInt())
        playerScores.map {
            pair ->
            newOpponentScore(pair.first,pair.second)
            if(pair.second>highestScore.second){
                highestScore=pair
            }
        }
        frag.ShowWinner(highestScore)
    }
    private fun newOpponentScore(id:String, playerScore:Int){
        frag.revealRoll(id,playerScore)
    }

    override fun sendMessage(message: Bundle) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun vibrate(tag:String,strength:Double){
        vibrator.vibrate(vibrationStrength)
        frag.opponentRolled(tag,strength,gameDuration)
    }
    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    constructor() {
        gameFragment = DiceFragment().newInstance(this)
        this.frag=gameFragment as DiceFragment
    }
    //When the player attempts to roll before the game starts
    private fun pregameRoll(){

    }
    //When the player attempts to roll
    private fun inGameRoll(event: SensorEvent){
        frag.diceRoll(event.values[0],event.values[1],accumulatedRollEnergy, this.gameDuration)
        sendOpponentRollData(accumulatedRollEnergy*(Random().nextDouble()+0.5))
    }
    private fun sendOpponentRollData(strength:Double){
        var message:Bundle= Bundle.EMPTY
        message.putString("type",messageStates.OPPONENT_SHAKE.code)
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
        vibrator = GameApp.applicationContext().getSystemService(VIBRATOR_SERVICE) as Vibrator
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