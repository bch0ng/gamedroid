package edu.us.ischool.bchong.info448project

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.widget.Button

class SelectPlayer_Activity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_selectplayer)

        val btnSingle = findViewById<Button>(R.id.btnSingle)
        val btnMulti = findViewById<Button>(R.id.btnMulti)

        btnSingle.setOnClickListener {
            val intent = Intent(this, GamelistActivity::class.java)
            startActivity(intent)
        }

        btnMulti.setOnClickListener{
            val fragmentManager = supportFragmentManager
            val transaction = fragmentManager.beginTransaction()

            val topic = intent.getStringExtra("item").replace(" ","")
            val fragmentHostOrGuest = FragmentHostOrGuest.newInstance()
            transaction.replace(R.id.fragments, fragmentHostOrGuest)
            transaction.commit()
        }
    }
}