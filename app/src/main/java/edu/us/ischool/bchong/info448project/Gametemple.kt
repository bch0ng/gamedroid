package edu.us.ischool.bchong.info448project

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast

class Gametemple : AppCompatActivity() {
    private lateinit var identity: String
    private lateinit var gamechoice: String
    private lateinit var beginGame: Button
    private lateinit var endGame: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gamechoice = this.intent.getStringExtra("GAME")
        identity = this.intent.getStringExtra("IDENTITY")
        beginGame = findViewById(R.id.buttonTest1)
        endGame = findViewById(R.id.buttonTest2)
        beginGame.setOnClickListener {
            Toast.makeText(this@Gametemple, "Gaming!", Toast.LENGTH_LONG).show()
        }
        endGame.setOnClickListener {
            val intent = Intent(this@Gametemple, Scoreboard_Activity::class.java)
            intent.putExtra("GAME", gamechoice)
            intent.putExtra("IDENTITY", identity)
            startActivity(intent)
        }
    }


}