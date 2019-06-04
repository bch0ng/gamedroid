package edu.us.ischool.bchong.info448project

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity


class GameSelectionActivity : AppCompatActivity(), GamelistFragment.OnFragmentInteractionListener {
    private lateinit var playMode: String
    private lateinit var identity: String
    private lateinit var username: String
    private lateinit var gameChoice: String
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_gamelist)
        playMode = this.intent.getStringExtra("PLAYMODE")
        identity = this.intent.getStringExtra("IDENTITY")
        username = this.intent.getStringExtra("USERNAME")
        gameChoice = this.intent.getStringExtra("GAME")
        var gamelist = GamelistFragment.newInstance(param1 = playMode, param2 = identity)
        supportFragmentManager.beginTransaction().replace(R.id.fragmentFrame, GamelistFragment(), "Gamelist").commit()
    }
}
