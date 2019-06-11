package edu.us.ischool.bchong.info448project

import android.content.Context.VIBRATOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import game.GameApp
import game.GameFragment
import java.util.*

//TODO Everything
class RollTheDiceClient : NetworkGame {

    override fun onDisconnect() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setId(id: String) {
        myId = id
    }

    enum class gameStates() {
        PREGAME(), INGAME(), POSTGAME()
    }

    private val diceRollVisualDuration=
        GameApp.applicationContext().getResources().getInteger(R.integer.dice_roll_visual_duration).toLong()
    private var pregameDuration = GameApp.applicationContext().getResources().getInteger(R.integer.dice_pregame_duration).toLong()
    var gameDuration = GameApp.applicationContext().getResources().getInteger(R.integer.dice_game_duration).toLong()
    private var postgameDuration = R.integer.dice_postgame_duration.toLong()

    var gameState = gameStates.PREGAME
    override var gameFragment: GameFragment? = null
    private var frag: DiceFragment
    private var accumulatedRollEnergy: Double = 0.0

    var score: Double = 0.0
    var prevAccelerations = arrayOf(0f, 0f, 0f)
    lateinit var myId: String
    lateinit var players: Array<Pair<String, String>>       //ID and name


    lateinit var vibrator: Vibrator
    val vibrationStrength =
        (GameApp.applicationContext().getResources().getInteger(R.integer.dice_vibration_strength)).toLong()

    //If this value is true then constants will be used to initialize the game
    //And shakes of your phone will trigger opponent shakes as well
    private val offlineTesting = true
    private var sendStartMessage = true
    override fun onPause() {
        if (vibrator != null) {
            vibrator.cancel()
        }
    }

    override fun onFragmentStart() {
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
        }
    }

    //When the game is started locally, before any server calls
    override fun onStart(name: String) {
        score = 0.0
        vibrator = GameApp.applicationContext().getSystemService(VIBRATOR_SERVICE) as Vibrator

        Log.v("test", "Dice Listener Started")
    }

    //When the game gets the starting signal from the server
    private fun startGame(message: Bundle) {
        players = message.getSerializable("players") as Array<Pair<String, String>>
        myId = message.getString("playerId")
        this.gameState = gameStates.INGAME
        val myName = message.getString("playerName") as String
        frag.StartGame(Pair(myId, myName), players)
        //This timer is for ending the game's pregame for visual effects
        Timer().schedule(object : TimerTask() {
            override fun run() {
                endPregame()
            }
        }, pregameDuration)
    }

    //When a new message is received from the server
    override fun newMessage(message: Bundle) {
        val type = message.get("type")
        when (type) {
            DiceNetworkMessages.START_GAME.code -> startGame(message)
            DiceNetworkMessages.OPPONENT_SHAKE.code -> vibrate(
                message.get("id") as String,
                message.get("strength") as Double
            )
            DiceNetworkMessages.NEW_TURN -> newTurn(message.get("id") as String)
            DiceNetworkMessages.OPPONENT_SCORE.code -> newOpponentScore(
                message.get("id") as String,
                message.get("score") as Int
            )
            DiceNetworkMessages.OPPONENT_DISCONNECT.code -> opponentDisconnect(message.getString("id"))
            DiceNetworkMessages.GAME_OVER.code -> gameOver(message.get("scores") as Array<Pair<String, Int>>)
            else -> Log.e("dice", "Invalid message $type")
        }
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

    //When the gameover message is recieved
    private fun gameOver(playerScores: Array<Pair<String, Int>>) {
        var highestScore = Pair<String, Int>(myId, score.toInt())
        playerScores.map { pair ->
            newOpponentScore(pair.first, pair.second)
            if (pair.second > highestScore.second) {
                highestScore = pair
            }
        }
        frag.showWinner(highestScore)
    }

    //Reveals a opponents score
    private fun newOpponentScore(id: String, playerScore: Int) {
        frag.revealRoll(id, playerScore)
    }

    override fun sendMessage(message: Bundle) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    //Vibrates
    fun vibrate(id: String, strength: Double) {
        Log.v("dice", "Vibrator strength $strength")
        vibrator.vibrate((vibrationStrength*strength).toLong())
        frag.opponentRolled(id, strength, diceRollVisualDuration)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    constructor() {
        gameFragment = DiceFragment().newInstance(this)
        this.frag = gameFragment as DiceFragment
    }

    //When the player attempts to roll before the game starts
    private fun pregameRoll() {

    }

    //When the player attempts to roll
    private fun inGameRoll(event: SensorEvent) {
        val strength = accumulatedRollEnergy * (Random().nextDouble() + 0.5)
        frag.diceRoll(event.values[0], event.values[1], accumulatedRollEnergy, this.diceRollVisualDuration)
        sendOpponentRollData(strength)

        //Remove later
        if (offlineTesting) {
            var bundle = Bundle()
            bundle.putString("type", DiceNetworkMessages.OPPONENT_SHAKE.code)
            bundle.putString("id", "p2")
            bundle.putDouble("strength", strength)
            newMessage(bundle)
        }
    }

    //Sends roll value to the server
    private fun sendOpponentRollData(strength: Double) {
        var message: Bundle = Bundle()
        message.putString("type", DiceNetworkMessages.OPPONENT_SHAKE.code)
        message.putString("id",this.myId)
        message.putDouble("strength", strength)
        sendMessage(message)
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
        if (increaseAccumulatedEnergy(event!!)) {
            when (gameState) {
                gameStates.PREGAME -> pregameRoll()
                gameStates.INGAME -> inGameRoll(event)
                gameStates.POSTGAME -> postGameRoll()

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

    override fun onRegisterMotionListener() {

    }

    private fun endPregame() {
        //TODO pregame ending visuals
    }

    private fun startPostgame() {
        frag.displayRestart(score.toInt(), 0, false)

    }

    override fun onEnd(): Int {
        return score.toInt()
    }

}