package edu.us.ischool.bchong.info448project

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button

class PlayModeActivity : AppCompatActivity() {

    private lateinit var playmode: String
    private lateinit var username: String
    private lateinit var singlebtn: Button
    private lateinit var multibtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playmode)

        username = this.intent.getStringExtra("USERNAME")
        singlebtn = findViewById(R.id.btnSingle)
        multibtn = findViewById(R.id.btnMulti)

        singlebtn.setOnClickListener {
            multibtn.isEnabled = false
            playmode = "Single"
            val intent = Intent(this@PlayModeActivity, GameSelectionActivity::class.java)
            intent.putExtra("PLAYMODE", playmode)
            intent.putExtra("USERNAME", username)
            intent.putExtra("IDENTITY", "Host")
            startActivity(intent)
        }

        multibtn.setOnClickListener {
            singlebtn.isEnabled = false
            playmode = "Multi"
            val intent = Intent(this@PlayModeActivity, Connection_activity::class.java)
            intent.putExtra("PLAYMODE", playmode)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
        }
    }
}