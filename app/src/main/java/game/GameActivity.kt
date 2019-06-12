package game

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import edu.us.ischool.bchong.info448project.*
import system.MainActivity

class GameActivity : AppCompatActivity(), GamelistFragment.OnGameInteractionListener,
    ScoreBoardFragment.OnScoreboardInteractionListener, GameStateController{

    private lateinit var game: Game
    private lateinit var identity: String
    private lateinit var mode: String
    private lateinit var username: String
    private lateinit var gamechoice:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        identity = intent.getStringExtra("IDENTITY")
        mode = intent.getStringExtra("GAMEMODE")
        if (mode == "Single") {
            username = intent.getStringExtra("USERNAME")
        } else if (mode == "Multi") {
            username = NearbyConnection.instance.getMyUsername()
        }
        Log.e("game", "The mode is" + mode)
        onGameSelect(mode, identity)
    }

    override fun goBackToMenu() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("MODE", "GAME_MENU")
        intent.putExtra("USERNAME", "TEST")
        startActivity(intent)
    }

    override fun playAgain(gameName: String) {
        onGameStart(gameName)
    }

    override fun onGameSelect(playmode: String, useridentity: String) {
        Log.e("game", "In onGameSelect")
        val gameSelectionFragment =
            GamelistFragment.newInstance(playmode, useridentity)
        supportFragmentManager
            .beginTransaction()
            .add(R.id.framegame, gameSelectionFragment!!, "game_fragment")
            .commit()
    }

    override fun onGameStart(gamechoice: String) {

        if (NearbyConnection.instance.isHosting()) {
            Log.i("TEST", "sending message: $gamechoice")

            NearbyConnection.instance.sendMessageAll("gamechoice: $gamechoice")
        }

        this.gamechoice = gamechoice
        Log.i("TEST", "gamechoice: $gamechoice")
        when(gamechoice){
            "Shake the Soda" -> game = SodaShake(this)
            "Flip the Phone" -> game = Flip()
            "RollTheDiceHost" -> game = RollTheDiceHost(this).localClient
            "Roll the Dice" -> game = RollTheDiceSinglePlayer(this)
            "Answer the Phone" -> game = Telephone(this)
        }
        var gameFragment = game.gameFragment as Fragment
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.framegame, gameFragment!!, "game_fragment")
            .commit()
        game.onStart(username)
    }

    private val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            var gameChoice = intent?.getStringExtra("GAME_CHOICE")

            if (gameChoice != null) {
                onGameStart(gameChoice!!)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(NearbyConnection.instance.getContext()).registerReceiver(broadCastReceiver,
            IntentFilter("edu.us.ischool.bchong.info448project.ACTION_SEND")
        )
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(NearbyConnection.instance.getContext()).unregisterReceiver(broadCastReceiver)
    }

    fun onGameResult(userscore: String) {
        val scoreBoardFragment = ScoreBoardFragment.newInstance(username, identity, gamechoice, userscore, mode)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.framegame, scoreBoardFragment!!, "game_fragment")
            .commit()
    }
    fun showScoreBoard(username: String,gamechoice: String,userscore: Int){
        onGameResult("0")
    }
}
