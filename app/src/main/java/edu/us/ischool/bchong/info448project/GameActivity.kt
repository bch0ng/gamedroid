package edu.us.ischool.bchong.info448project

import android.net.Uri

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : FragmentActivity(),FlipFragment.OnFragmentInteractionListener {
    override fun onFragmentInteraction(uri: Uri) {
        //Something
    }

    lateinit var game:Game


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        game = intent.extras.getSerializable("game") as Game

        var gameFragment = game.gameFragment as Fragment
        back_button.setOnClickListener {

        }
        supportFragmentManager
            .beginTransaction()
            .add(R.id.game_frame, gameFragment!!, "game_fragment")
            .commit()
        game.onStart(getString(R.string.default_player_name))
    }



    override fun onDestroy() {
        super.onDestroy()
        game.OnEnd()
    }
}
