package edu.us.ischool.bchong.info448project

import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import game.Game
import game.GameApp
import game.GameApp.Companion.applicationContext
import game.GameFragment
import java.security.AccessController.getContext
import java.util.*
import kotlin.collections.ArrayList

//TODO Everything
class RollTheDiceSinglePlayer : Game {
    override fun onRegisterMotionListener() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val diceRollVisualDuration:Long=1000
    private var pregameDuration:Long = 1000
    var gameDuration:Long = 3000
    private var postgameDuration:Long = 1000

    override var gameFragment: GameFragment? = null
    private var frag: SinglePlayerDiceFragment
    private var accumulatedRollEnergy: Double = 0.0

    var score: Double = 0.0
    var prevAccelerations = arrayOf(0f, 0f, 0f)
    lateinit var myId: String
    lateinit var players: ArrayList<Pair<String, String>>       //ID and name


    lateinit var vibrator: Vibrator
    val vibrationStrength =
        (1000).toLong()

    //If this value is true then constants will be used to initialize the game
    //And shakes of your phone will trigger opponent shakes as well
    private val offlineTesting = false
    private var context: Context? = null

    private var sendStartMessage = true
    override fun onPause() {
        if (vibrator != null) {
            vibrator.cancel()
        }
    }

    override fun onFragmentStart() {/*
        if (sendStartMessage && offlineTesting) {
            sendStartMessage = false
            Log.v("dice", "offline testing started.")
            var newBundle: Bundle = Bundle()
            var testPlayers = arrayOf(Pair("Player", "me"), Pair("p2", "Ted"), Pair("p3", "NotTed"))
            newBundle.putString("type", DiceNetworkMessages.START_GAME.code)
            newBundle.putSerializable("players", testPlayers)
            newBundle.putString("playerId", "Player")
            newBundle.putString("playerName", "Player")
            newMessage(newBundle)
        }*/
    }

    //When the game is started locally, before any server calls
    override fun onStart(name: String) {
        score = 0.0
        vibrator=context!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        Log.v("test", "Dice Listener Started")
    }


    //When it is a player's turn, display their name
    private fun newTurn(id: String){
        players.map {
            if(it.first==id){
                frag.displayNewTurn(it.second)
            }
        }
    }

    private fun opponentDisconnect(id: String) {
        //frag.opponentDisconnected(id)
    }

    //Vibrates
    fun vibrate(id: String, strength: Double) {
        Log.v("dice", "Vibrator strength $strength")
        vibrator.vibrate((vibrationStrength*strength).toLong())
        frag.opponentRolled(id, strength, diceRollVisualDuration)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    constructor(thisContext:Context) {
        gameFragment = SinglePlayerDiceFragment().newInstance(this)
        this.frag = gameFragment as SinglePlayerDiceFragment
        this.context=thisContext
    }

    //When the player attempts to roll before the game starts
    private fun pregameRoll() {

    }

    //When the player attempts to roll
    private fun inGameRoll(event: SensorEvent) {
        val strength = accumulatedRollEnergy * (Random().nextDouble() + 0.5)
        vibrator.vibrate((vibrationStrength*strength).toLong())
        frag.diceRoll(event.values[0], event.values[1], accumulatedRollEnergy, this.diceRollVisualDuration)

    }
    //When the player attempts to roll after the game is completed
    private fun postGameRoll() {
        //TODO postgame roll visuals
    }

    //Was the dice rolled? based on an acceleration change
    private fun isRoll(values: FloatArray): Boolean {
        val rollVel=values[0] + values[1] + values[2]
        //Log.v("diceRoll","Roll $rollVel")
        return rollVel > 20.0
    }

    private fun increaseAccumulatedEnergy(event: SensorEvent): Boolean {
        val eventValues = event.values
        var roll = isRoll(eventValues)
        if (roll) {
            accumulatedRollEnergy += 0.4 * (eventValues[0] + eventValues[1] + eventValues[2])
        }
        return roll
    }

    private fun accelerationEvent(event: SensorEvent?) {

        inGameRoll(event!!)
    }


    override fun onSensorChanged(event: SensorEvent?) {
        //Log.v("test", "sensorchanged")
        val type = event!!.sensor.type
        if (type == Sensor.TYPE_ACCELEROMETER) {
            accelerationEvent(event)
        }
    }


    override fun onEnd(): Int {
        return score.toInt()
    }
}

