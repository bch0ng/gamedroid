package edu.us.ischool.bchong.info448project

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


class GameSelectionFragment : Fragment() {
    private lateinit var playMode: String
    private lateinit var identity: String
    //private lateinit var username: String
    private lateinit var gameChoice: String

    companion object {
        fun newInstance(username: String, playMode: String, identity: String?): PlayModeFragment {
            val fragment = PlayModeFragment()
            val bundle = Bundle()
            bundle.putString("USERNAME", username)
            bundle.putString("PLAYMODE", playMode)
            bundle.putString("IDENTITY", identity)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_gamelist, container, false)
        handleView(view)
        return view
    }

    fun handleView(view: View) {
        playMode = arguments!!.getString("PLAYMODE")
        identity = arguments!!.getString("IDENTITY")
        // username = this.intent.getStringExtra("USERNAME")
        // gameChoice = this.intent.getStringExtra("GAME")
        var gamelist = GamelistFragment.newInstance(param1 = playMode, param2 = identity)
        Log.d("GameSystem", "I am in instance of fragment")
        fragmentManager!!.beginTransaction().replace(R.id.fragmentFrame, gamelist, "Gamelist").commit()
    }
}
