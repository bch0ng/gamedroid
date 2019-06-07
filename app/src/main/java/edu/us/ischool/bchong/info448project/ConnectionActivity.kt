package edu.us.ischool.bchong.info448project

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
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

class ConnectionActivity : AppCompatActivity() {

    private lateinit var buttonAdvertise: Button
    private lateinit var buttonDiscover: Button
    private lateinit var buttonStop: Button
    private lateinit var buttonOtherActivity: Button
    private lateinit var roomCodeField: EditText
    private lateinit var roomCodeShow: TextView

    private lateinit var nearby: NearbyConnection

    private var endpointID: ArrayList<String> = ArrayList()
    private var broadcastMessage: String = ""


    override fun onStart()
    {
        super.onStart()
        if (Build.VERSION.SDK_INT < 28) {
            if (ContextCompat.checkSelfPermission(
                    this@ConnectionActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@ConnectionActivity,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    8034
                )
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this@ConnectionActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@ConnectionActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    8035
                )
            }
        }
    }

    val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            Log.d("INFO_448_DEBUG", "Broadcast message received: ${intent?.getStringExtra("message")}")
            roomCodeShow.text = intent?.getStringExtra("message")
            Toast.makeText(this@ConnectionActivity, intent?.getStringExtra("message"), Toast.LENGTH_SHORT).show()
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

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connection)
        NearbyConnection.initialize(this)
        nearby = NearbyConnection.instance

        buttonAdvertise = findViewById(R.id.buttonAdvertise)
        buttonDiscover = findViewById(R.id.buttonDiscover)
        buttonStop = findViewById(R.id.buttonStop)
        buttonOtherActivity = findViewById(R.id.buttonOtherActivity)
        roomCodeField = findViewById(R.id.roomCodeText)
        roomCodeShow = findViewById(R.id.roomCodeShow)

        var mode = "none"
        buttonAdvertise.setOnClickListener {
            mode = "advertising"
            val roomCode = nearby.startAdvertising()
            buttonDiscover.isEnabled = false
            buttonDiscover.visibility = View.GONE
            buttonAdvertise.isEnabled = false
            buttonStop.isEnabled = true
            buttonOtherActivity.isEnabled = true
            roomCodeField.visibility = View.GONE
            roomCodeShow.visibility = View.VISIBLE
            roomCodeShow.text = roomCode
        }
        roomCodeField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                buttonDiscover.isEnabled = (s.toString().length == 4)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        buttonDiscover.setOnClickListener {
            mode = "discovery"
            nearby.startDiscovery(roomCodeField.text.toString())
            buttonDiscover.isEnabled = false
            buttonAdvertise.isEnabled = false
            buttonAdvertise.visibility = View.GONE
            buttonStop.isEnabled = true
            buttonOtherActivity.isEnabled = true
            roomCodeField.isEnabled = false
        }
        buttonStop.setOnClickListener {
            if (mode == "advertising")
                nearby.stopAdvertising()
            else if (mode == "discovery")
                nearby.stopDiscovery()
            nearby.disconnectEndpoints()
            mode = "none"
            buttonAdvertise.isEnabled = true
            buttonDiscover.isEnabled = true
            buttonDiscover.visibility = View.VISIBLE
            buttonOtherActivity.isEnabled = false
            buttonStop.isEnabled = false
            buttonAdvertise.visibility = View.VISIBLE
            roomCodeField.visibility = View.VISIBLE
            roomCodeField.isEnabled = true
            roomCodeShow.visibility = View.GONE
        }
        buttonOtherActivity.setOnClickListener {
            broadcastMessage = "Hello, world!"
            nearby.sendMessageAll(broadcastMessage)
            val intent = Intent(this, TestActivity::class.java).apply {
                putExtra(EXTRA_MESSAGE, broadcastMessage)
            }
            startActivity(intent)
        }
    }
}