package Game

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import edu.us.ischool.bchong.info448project.R

class GameActivity : AppCompatActivity(), GamelistFragment.OnGameInteractionListener, ScoreBoardFragment.OnScoreboardInteractionListener{
    /*override fun onFragmentInteraction(uri: Uri) {
        //Something
}*/

    private lateinit var game: Game
    private lateinit var identity: String
    private lateinit var mode:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        identity = intent.getStringExtra("IDENTITY")
        mode = intent.getStringExtra("GAMEMODE")
        onGameSelect(identity,mode)
    }

    override fun onGameSelect(playmode: String,useridentity: String) {

        /*game = intent.extras.getSerializable("GAME") as Game
        var gameFragment = game.gameFragment as Fragment*/
        if(playmode == "Single" && useridentity=="Host"){
            val gameSelectionFragment =
                GamelistFragment.newInstance(playmode, useridentity)
            supportFragmentManager
                .beginTransaction()
                .add(R.id.game_frame, gameSelectionFragment!!, "game_fragment")
                .commit()
        }else if(playmode == "Multi" && useridentity =="Host"){
            //TODO
        }else{
            //TODO
        }
    }

    override fun onGamestart(gamechoice: String){
        if(gamechoice == "Shake the Soda"){
            game = SodaShake(this)
        }
        var gameFragment = game.gameFragment as Fragment
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.game_frame, gameFragment!!, "game_fragment")
            .commit()
        game.OnStart()
    }

    override fun onGameResult(username:String,useridentity:String,gamechoice: String,userscore:String, playmode:String){
        val scoreBoardFragment = ScoreBoardFragment.newInstance(username,useridentity, gamechoice, userscore, playmode)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.game_frame, scoreBoardFragment!!, "game_fragment")
            .commit()
    }

    override fun onEndCycle(){
        game.OnEnd()
        onDestroy()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
