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
    private lateinit var sendButton: Button
    private lateinit var closeButton: Button

    private lateinit var nearby: NearbyConnection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        nearby = NearbyConnection.instance

        textShow = findViewById(R.id.textShow)
        sendButton = findViewById(R.id.buttonSend)
        closeButton = findViewById(R.id.buttonClose)

        sendButton.setOnClickListener {
            nearby.sendMessageAll("test:Hello Mars")
        }
        closeButton.setOnClickListener {
            finish()
        }
    }

    val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            Log.d("INFO_448_DEBUG", "Broadcast message received: ${intent?.getStringExtra("message")}")
            textShow.text = intent?.getStringExtra("messag")
            Toast.makeText(this@TestActivity, intent?.getStringExtra("message"), Toast.LENGTH_SHORT).show()
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