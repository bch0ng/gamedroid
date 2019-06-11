package edu.us.ischool.bchong.info448project

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import game.Game
import game.GameFragment
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class TelephoneFragment : Fragment(), GameFragment {
    var gameObj: Game? = null
    var gameMessage: TextView? = null
    var nearby: NearbyConnection? = null

    override fun newInstance(game: Game): GameFragment {
        nearby = NearbyConnection.instance
        gameObj = game
        return this
    }

    fun showWinText() {
        NearbyConnection.instance.sendMessageAll("telephone: This is a test")
        Log.i("TEST", "Message sent")
        gameMessage?.setText("You win!")
    }

    fun showLoseText() {
        gameMessage?.setText("You lose!")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_telephone, container, false)

        gameMessage = view.findViewById(R.id.telephone_message)

        return view
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(nearby!!.getContext()).registerReceiver(broadCastReceiver,
            IntentFilter("edu.us.ischool.bchong.info448project.ACTION_SEND")
        )
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(nearby!!.getContext()).unregisterReceiver(broadCastReceiver)
    }


    private val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            Log.i("TEST", intent?.getStringExtra("TELEPHONE_RING"))
        }
    }


}