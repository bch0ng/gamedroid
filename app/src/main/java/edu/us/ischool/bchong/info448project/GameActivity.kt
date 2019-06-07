package edu.us.ischool.bchong.info448project

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.widget.Button
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : FragmentActivity(),FlipFragment.OnFragmentInteractionListener {
    override fun onFragmentInteraction(uri: Uri) {
        //Something
    }

    lateinit var game:Game
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        if (intent.getStringExtra("game") == "SODA_SHAKE") {
            game = SodaShake(this)
        } else {
            game = intent.extras.getSerializable("game") as Game
        }

        var gameFragment = game.gameFragment as Fragment
        findViewById<Button>(R.id.back_button).setOnClickListener {
            game.OnEnd()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        supportFragmentManager
            .beginTransaction()
            .add(R.id.game_frame, gameFragment!!, "game_fragment")
            .commit()
        game.OnStart()
    }



    override fun onDestroy() {
        super.onDestroy()
        game.OnEnd()
    }
}
