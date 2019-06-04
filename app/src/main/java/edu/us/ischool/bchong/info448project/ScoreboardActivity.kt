package edu.us.ischool.bchong.info448project

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button

class ScoreboardActivity : AppCompatActivity() {
    private lateinit var gamechoice: String
    private lateinit var identity: String
    private lateinit var restartButton: Button
    private lateinit var newGameButton: Button
    private var gameMap = hashMapOf<String, Activity>(
        "Shake the Soda" to Gametemple(),
        "Flip the Phone" to Gametemple(),
        "Answer the Phone" to Gametemple(),
        "Roll the Dice" to Gametemple()
    )

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_scoreboard)
        restartButton = findViewById(R.id.buttonRestart)
        newGameButton = findViewById(R.id.buttonNew)
        gamechoice = this.intent.getStringExtra("GAME")
        identity = this.intent.getStringExtra("IDENTITY")
        if (identity == "Host") {
            restartButton.isEnabled = true
            newGameButton.isEnabled = true
            restartButton.visibility = View.VISIBLE
            newGameButton.visibility = View.VISIBLE
        } else {
            restartButton.isEnabled = false
            newGameButton.isEnabled = false
            restartButton.visibility = View.GONE
            newGameButton.visibility = View.GONE
        }

        restartButton.setOnClickListener {
            newGameButton.isEnabled = false
            val intent = Intent(this@ScoreboardActivity, gameMap.get(gamechoice)!!::class.java)
            intent.putExtra("GAME", gamechoice)
            intent.putExtra("IDENTITY", identity)
            startActivity(intent)
        }
        newGameButton.setOnClickListener {
            restartButton.isEnabled = false
            // What if the player wants to switch between single mode and multi mode?
            val intent = Intent(this@ScoreboardActivity, GameSelectionActivity::class.java)
            intent.putExtra("IDENTITY", identity)
            startActivity(intent)
        }
    }

    private fun winnerCounter() {

    }

}