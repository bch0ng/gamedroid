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
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

//Game controller for roll the dice
//WARNING: Use newInstance to set this phone's local gameclient
class RollTheDiceHost : GameHost {
    lateinit var players: Array<Pair<String, String>>       //ID and name
    private lateinit var nearby: NearbyConnection

    //Sets the players
    override fun setPlayers(playerData: ArrayList<Pair<String, String>>) {
        players = (playerData.toArray() as Array<Pair<String, String>>)
    }

    override fun kickPlayer(id: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    enum class gameStates() {
        PREGAME(), INGAME(), POSTGAME()
    }

    //Possible messages codes this server can recieve
    enum class messageStates(val code: String) {
        NEW_SHAKE("newShake"),
        PLAYER_DISCONNECT("playerDisconnect")
    }

    lateinit override var localClient: NetworkGame
    private var pregameDuration = R.integer.dice_pregame_duration.toLong()
    private var gameDuration = R.integer.dice_game_duration.toLong()

    var gameState = gameStates.PREGAME

    lateinit var myId: String
    lateinit var playerScores: MutableMap<String, Int>
    var turnIndex=0
    //Sets the local client to the given game and returns this instance
    override fun newInstance(game: NetworkGame): GameHost {
        this.localClient = game
        return this
    }

    override fun onEnd() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun newMessage(message: Bundle) {
        val type = message.get("type")
        when (type) {
            messageStates.NEW_SHAKE.code -> newShake(message)
            messageStates.PLAYER_DISCONNECT.code -> playerDisconnect(message)
        }
    }

    private fun randomDiceRoll(): Int {
        return (Math.random() * R.integer.dice_max_value + 1.0).toInt()
    }

    private fun playerDisconnect(message: Bundle) {
        val sender = message.getString("id")
        players.map {
            if (it.first != sender) {
                var newMessage: Bundle = Bundle()
                newMessage.putString("type", DiceNetworkMessages.OPPONENT_DISCONNECT.code)
                newMessage.putString("id", sender)
                newMessage.putInt("strength", randomDiceRoll())
                sendMessage(newMessage)
            }
        }
    }

    private fun newShake(message: Bundle) {
        val sender = message.getString("id")
        val roll = randomDiceRoll()
        if(sender==players[turnIndex].first){
            players.map {
                if (it.first != sender) {
                    var newMessage: Bundle = Bundle()
                    newMessage.putString("type", DiceNetworkMessages.OPPONENT_SHAKE.code)
                    newMessage.putString("id", sender)
                    newMessage.putInt("strength", roll)
                    sendMessage(newMessage)
                }
            }
        }
    }

    private fun nextTurn() {
        if (turnIndex > players.size) {
            gameOver()
        } else {
            var newTurnBundle=Bundle()
            newTurnBundle.putString("type",DiceNetworkMessages.NEW_TURN.code)
            newTurnBundle.putString("id",players[turnIndex].first)
            sendMessage(newTurnBundle)
            Timer("turnTimer", false).schedule(gameDuration) {
                turnIndex += 1
                nextTurn()
            }
        }
    }

    //When the game is over send a message to all players with everyone's id and score
    private fun gameOver() {
        var scores = playerScores.keys.iterator()
        var baseMessage: Bundle = Bundle()
        baseMessage.putString("type", DiceNetworkMessages.GAME_OVER.code)
        var scoresArray: Array<Pair<String, Int>> = arrayOf<Pair<String, Int>>()
        var count = 0
        while (scores.hasNext()) {
            val id = scores.next()
            val value = playerScores.get(id)
            scoresArray[count] = Pair<String, Int>(id, value!!)
            count++
        }
        baseMessage.putSerializable("scores", scoresArray)
        players.map {
            sendMessage(baseMessage)
        }
    }

    override fun sendMessage(message: Bundle) {
        nearby.sendMessageAll(message.toString())
    }

    constructor() {
        //TODO: add check if gamefragment is set correctly

        this.localClient = RollTheDiceClient()
        this.localClient.gameFragment!!.setNetworkListener(this)
    }


    //When everything is initialized
    //TODO Remember to call this method from the activity or fragment!!!!!!!!!!!!!!!!!!!!!!!!!!!
    override fun onStart() {
        Timer("endPregame", false).schedule(pregameDuration) {
            gameState = gameStates.INGAME
            nextTurn()
            endPregame()
        }
        this.playerScores = hashMapOf<String, Int>()
        players.map {
            playerScores.put(it.first,randomDiceRoll())
        }
        Log.v("test", "Dice Listener Started")
    }

    private fun tellPlayersGameHasStarted() {
        //TODO: SEND THE DiceNetworkMessages.START_GAME.code MESSAGE to all the players
    }

    private fun endPregame() {
        gameState = gameStates.INGAME
    }

    private fun startPostgame() {
        onEnd()
    }
}