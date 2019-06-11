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
import android.widget.Toast
import game.GameActivity


class TelephoneFragment : Fragment(), GameFragment {
    override fun setNetworkListener(networkListener: NetworkListener) {
       // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    var gameObj: Game? = null
    var gameMessage: TextView? = null

    override fun newInstance(game: Game): GameFragment {
        gameObj = game
        return this
    }

    fun showWinText() {
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

    private val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i("TEST", "timeDelay: ${intent?.getStringExtra("TELEPHONE_TIME")?.toLong()}")
            var timeDelay = intent?.getStringExtra("TELEPHONE_TIME")?.toLong()
            if (timeDelay != null) {
                (gameObj as Telephone).setTime(timeDelay)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(NearbyConnection.instance.getContext()).registerReceiver(broadCastReceiver,
            IntentFilter("edu.us.ischool.bchong.info448project.ACTION_SEND")
        )
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(NearbyConnection.instance.getContext()).unregisterReceiver(broadCastReceiver)
    }

}