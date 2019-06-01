package edu.us.ischool.bchong.info448project

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        single_player.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            var game=Flip()
            intent.putExtra("game", game)
            startActivity(intent)
        }
        roll_dice.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            var game=RollTheDiceClient()
            intent.putExtra("game", game)
            startActivity(intent)
        }
    }
}
