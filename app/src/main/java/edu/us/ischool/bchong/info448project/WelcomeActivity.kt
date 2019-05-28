package edu.us.ischool.bchong.info448project

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_welcome)

        val btnStartGame = findViewById<Button>(R.id.btnStartGame)
        val enterName = findViewById<EditText>(R.id.editText2)
        val userName = enterName.text.toString()

        btnStartGame.setOnClickListener {
            val intent = Intent(this, SelectPlayer_Activity::class.java)
            intent.putExtra("UserName", userName)
            startActivity(intent)
        }
    }
}