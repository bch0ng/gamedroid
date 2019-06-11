package edu.us.ischool.bchong.info448project

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.nearby.Nearby

class TestActivity : AppCompatActivity() {

    private lateinit var textShow: TextView
    private lateinit var textShow2: TextView
    private lateinit var sendButton: Button
    private lateinit var closeButton: Button

    private lateinit var nearby: NearbyConnection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        textShow = findViewById(R.id.textShow)
        textShow2 = findViewById(R.id.textShow2)
        sendButton = findViewById(R.id.buttonSend)
        closeButton = findViewById(R.id.buttonClose)

        nearby = NearbyConnection.instance
        if (nearby.isHosting()) {
            textShow.text = "${nearby.getMyUsername()} (You are the Host)"
        } else {
            val players = nearby.getCurrPlayers()
            textShow.text = ""
            for (player in players) {
                textShow.text = textShow.text.toString() + "\n" + player
            }
        }

        val message = intent.getStringExtra("roomCode")
        textShow2.text = message

        sendButton.setOnClickListener {
            nearby.sendMessageAll("test:Hello Mars")
            val players = nearby.getCurrPlayers()
            textShow.text = ""
            for (player in players) {
                textShow.text = textShow.text.toString() + "\n" + player
            }
        }
        closeButton.setOnClickListener {
            finish()
        }
    }

    val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            if (intent?.hasExtra("message")!!) {
                Log.d("INFO_448_DEBUG", "Broadcast message received: ${intent?.getStringExtra("message")}")
                val message = intent?.getStringExtra("message")
                if (message?.startsWith("updateRoom:")!!) {
                    val players = nearby.getCurrPlayers()
                    textShow.text = ""
                    for ((index, player) in players.withIndex()) {
                        if (index == 0 && nearby.isHosting()) {
                            textShow.text = "$player (You are the Host)"
                        } else {
                            textShow.text = textShow.text.toString() + "\n" + player
                        }
                    }
                }
                Toast.makeText(this@TestActivity, intent?.getStringExtra("message"), Toast.LENGTH_SHORT).show()
            } else if (intent.hasExtra("roomCode")) {
                Log.d("INFO_448_DEBUG", "Broadcast message received: ${intent?.getStringExtra("roomCode")}")
                if (textShow2.text.toString().isBlank()) {
                    val message = intent?.getStringExtra("roomCode")
                    textShow2.text = message
                }
            }
        }
    }

    override fun onResume()
    {
        super.onResume()
        LocalBroadcastManager.getInstance(nearby.getContext()).registerReceiver(broadCastReceiver,
            IntentFilter("edu.us.ischool.bchong.info448project.ACTION_SEND"))
    }

    override fun onPause()
    {
        super.onPause()
        LocalBroadcastManager.getInstance(nearby.getContext()).unregisterReceiver(broadCastReceiver)
    }
}