package Game

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import edu.us.ischool.bchong.info448project.R

class GameActivity : FragmentActivity(), GamelistFragment.OnGameInteractionListener, ScoreBoardFragment.OnScoreboardInteractionListener{
    /*override fun onFragmentInteraction(uri: Uri) {
        //Something
}*/

    private lateinit var game: Game
    private lateinit var identity: String
    private lateinit var mode:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        onGameSelect()
    }

    override fun onGameSelect() {
        identity = intent.getStringExtra("IDENTITY")
        mode = intent.getStringExtra("GAMEMODE")
        /*game = intent.extras.getSerializable("GAME") as Game
        var gameFragment = game.gameFragment as Fragment*/
        if(mode == "Single" && identity=="Host"){
            val gameSelectionFragment =
                GamelistFragment.newInstance(mode, identity)
            supportFragmentManager
                .beginTransaction()
                .add(R.id.game_frame, gameSelectionFragment!!, "game_fragment")
                .commit()
        }else if(mode == "Multi" && identity =="Host"){
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

    override fun onGameResult(){
        val scoreBoardFragment = ScoreBoardFragment.newInstance()
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
