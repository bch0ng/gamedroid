package edu.us.ischool.bchong.info448project

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button

class PlayModeActivity : AppCompatActivity() {

    private lateinit var playmode: String
    private lateinit var username: String
    private lateinit var singlebtn: Button
    private lateinit var multibtn: Button

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_playmode)

        singlebtn = findViewById(R.id.btnSingle)
        multibtn = findViewById(R.id.btnMulti)

        singlebtn.setOnClickListener {
            multibtn.isEnabled = false
            playmode = "single"
            val intent = Intent(this@PlayModeActivity, GameSelectionActivity::class.java)
            intent.putExtra("PLAYMODE", playmode)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
        }

        multibtn.setOnClickListener {
            singlebtn.isEnabled = false
            playmode = "multi"
            val intent = Intent(this@PlayModeActivity, Connection_activity::class.java)
            intent.putExtra("PLAYMODE", playmode)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
        }
    }
}