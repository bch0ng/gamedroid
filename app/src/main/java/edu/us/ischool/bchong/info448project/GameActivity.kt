package edu.us.ischool.bchong.info448project

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class GameActivity : AppCompatActivity() {
    lateinit var game:Game
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        game = intent.extras.getSerializable("game") as Game
        var gameFragment = game.gameFragment
        game.gameFragment=gameFragment
        game.OnStart()
    }



    override fun onDestroy() {
        super.onDestroy()
        game.OnEnd()
    }
}
