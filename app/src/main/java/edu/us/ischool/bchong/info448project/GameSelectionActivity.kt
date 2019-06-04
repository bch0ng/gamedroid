package edu.us.ischool.bchong.info448project

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log


class GameSelectionActivity : AppCompatActivity(), GamelistFragment.OnFragmentInteractionListener {
    private lateinit var playMode: String
    private lateinit var identity: String
    //private lateinit var username: String
    private lateinit var gameChoice: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gamelist)
        playMode = this.intent.getStringExtra("PLAYMODE")
        identity = this.intent.getStringExtra("IDENTITY")
        // username = this.intent.getStringExtra("USERNAME")
        // gameChoice = this.intent.getStringExtra("GAME")
        var gamelist = GamelistFragment.newInstance(param1 = playMode, param2 = identity)
        Log.d("GameSystem", "I am in instance of fragment")
        supportFragmentManager.beginTransaction().replace(R.id.fragmentFrame, gamelist, "Gamelist").commit()
    }
}
