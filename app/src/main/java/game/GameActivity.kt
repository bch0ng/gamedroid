package game

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import edu.us.ischool.bchong.info448project.NearbyConnection
import edu.us.ischool.bchong.info448project.R
import edu.us.ischool.bchong.info448project.Telephone

class GameActivity : AppCompatActivity(), GamelistFragment.OnGameInteractionListener,
    ScoreBoardFragment.OnScoreboardInteractionListener {

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
        this.gamechoice = gamechoice
        Log.i("TEST", "gamechoice: $gamechoice")
        when(gamechoice){
            "Shake the Soda" -> game = SodaShake(this)
            "Flip the Phone" -> game = Telephone(this)
            //TODO "Answer the Phone" and " Roll the Dice"
        }
        var gameFragment = game.gameFragment as Fragment
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.framegame, gameFragment!!, "game_fragment")
            .commit()
        game.onStart(getString(R.string.default_player_name))
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


    override fun onEndCycle() {
        game.onEnd()
    }
}
