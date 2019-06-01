package edu.us.ischool.bchong.info448project

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button

class SelectPlayer_Activity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_selectplayer)

        val btnSingle = findViewById<Button>(R.id.btnSingle)
        val btnMulti = findViewById<Button>(R.id.btnMulti)
    }
}