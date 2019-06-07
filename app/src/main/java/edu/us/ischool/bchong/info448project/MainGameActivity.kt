package edu.us.ischool.bchong.info448project

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainGameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        single_player.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            var game = Flip()
            intent.putExtra("game", game)
            startActivity(intent)
        }
    }

    fun startSodaShake(view: View?) {
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("game", "SODA_SHAKE")
        startActivity(intent)
    }

}
