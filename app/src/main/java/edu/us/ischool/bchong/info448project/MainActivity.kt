package edu.us.ischool.bchong.info448project

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.Strategy.P2P_STAR
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback

const val USER_NICKNAME: String = "info448project"
const val SERVICE_ID: String    = "edu.us.ischool.bchong.info448project"

class MainActivity : AppCompatActivity()
{
    private lateinit var buttonAdvertise:   Button
    private lateinit var buttonDiscover:    Button
    private lateinit var buttonStop:        Button
    private lateinit var buttonBroadcast:   Button

    private var endpointID: ArrayList<String> = ArrayList()
    private var broadcastMessage: String = ""

    override fun onStart()
    {
        super.onStart()
        if (Build.VERSION.SDK_INT < 28) {
            if (ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    8034
                )
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    8035
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonAdvertise = findViewById(R.id.button)
        buttonDiscover = findViewById(R.id.button2)
        buttonStop = findViewById(R.id.button3)
        buttonBroadcast = findViewById(R.id.button4)

        var mode = "none"
        buttonAdvertise.setOnClickListener {
            mode = "advertising"
            startAdvertising()
            buttonDiscover.isEnabled = false
            buttonDiscover.visibility = View.GONE
            buttonAdvertise.isEnabled = false
            buttonStop.isEnabled = true
        }
        buttonDiscover.setOnClickListener {
            mode = "discovery"
            startDiscovery()
            buttonDiscover.isEnabled = false
            buttonAdvertise.isEnabled = false
            buttonAdvertise.visibility = View.GONE
            buttonStop.isEnabled = true
        }
        buttonStop.setOnClickListener {
            if (mode == "advertising")
                stopAdvertising()
            else if (mode == "discovery")
                stopDiscovery()
            else if (mode == "broadcast")
                stopBroadcast()
            mode = "none"
            buttonAdvertise.isEnabled = true
            buttonDiscover.isEnabled = true
            buttonDiscover.visibility = View.VISIBLE
            buttonAdvertise.visibility = View.VISIBLE
        }
        buttonBroadcast.setOnClickListener {
            mode = "broadcast"
            broadcastMessage = "Hello, world!"
            val bytesPayload = Payload.fromBytes(broadcastMessage.toByteArray(Charsets.UTF_8))
            for (id in endpointID) {
                Nearby.getConnectionsClient(this@MainActivity).sendPayload(id, bytesPayload)
            }
        }
    }

    private fun stopBroadcast() {
        val nearby = Nearby.getConnectionsClient(this@MainActivity)
            nearby.stopAdvertising()
            nearby.stopAllEndpoints()
            for (id in endpointID) {
                nearby.disconnectFromEndpoint(id)
            }
    }

    private fun startAdvertising()
    {
        val advertisingOptions = AdvertisingOptions.Builder().setStrategy(P2P_STAR).build()

        Nearby.getConnectionsClient(this@MainActivity)
            .startAdvertising(
                USER_NICKNAME, SERVICE_ID, connectionLifecycleCallback, advertisingOptions
            )
            .addOnSuccessListener { unused: Void? ->
                // We're advertising!
                Log.d("INFO_448_DEBUG", "Advertised!")
            }
            .addOnFailureListener { e: Exception ->
                // We were unable to start advertising.
                Log.d("INFO_448_DEBUG", "Not Advertising\n" + e.message)
            }
    }

    private fun stopAdvertising()
    {
        Nearby.getConnectionsClient(this@MainActivity).stopAdvertising()
        Log.d("INFO_448_DEBUG", "Stopped Advertising")
    }

    private fun startDiscovery()
    {
        val discoveryOptions = DiscoveryOptions.Builder().setStrategy(P2P_STAR).build()
        Nearby.getConnectionsClient(this@MainActivity)
            .startDiscovery(SERVICE_ID, endpointDiscoveryCallback, discoveryOptions)
            .addOnSuccessListener { unused: Void? ->
                // We're discovering!
                Log.d("INFO_448_DEBUG", "Discovered")
            }
            .addOnFailureListener { e: Exception ->
                // We're unable to start discovering.
                Log.d("INFO_448_DEBUG", "Not Discovering\n" + e.message)
            }
    }

    private fun stopDiscovery()
    {
        Nearby.getConnectionsClient(this@MainActivity).stopDiscovery()
        Log.d("INFO_448_DEBUG", "Stopped Discovering")
    }

    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback()
    {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo)
        {
            // An endpoint was found. We request a connection to it.
            Log.d("INFO_448_DEBUG", "Endpoint Found")
            Nearby.getConnectionsClient(this@MainActivity)
                .requestConnection(USER_NICKNAME, endpointId, connectionLifecycleCallback)
                .addOnSuccessListener { unused: Void? ->
                    // We successfully requested a connection. Now both sides
                    // must accept before the connection is established.
                    endpointID.add(endpointId)
                    stopDiscovery()
                    buttonStop.isEnabled = false
                    buttonBroadcast.isEnabled = true
                    buttonBroadcast.visibility = View.VISIBLE
                    buttonAdvertise.visibility = View.GONE
                    buttonDiscover.visibility = View.GONE
                    buttonStop.visibility = View.GONE

                    Log.d("INFO_448_DEBUG", "Connection success (join)")
                }
                .addOnFailureListener { e: Exception ->
                    // Nearby Connections failed to request the connection.
                    Log.d("INFO_448_DEBUG", "Connection Failed\n" + e.message)
                }
        }

        override fun onEndpointLost(endpointId: String)
        {
            // A previously discovered endpoint has gone away.
            Log.d("INFO_448_DEBUG", "Endpoint Lost")
        }
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback()
    {
        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo)
        {
            // Automatically accept the connection on both sides.
            Nearby.getConnectionsClient(this@MainActivity).acceptConnection(endpointId, ReceiveBytesPayloadListener())
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution)
        {
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    endpointID.add(endpointId)
                    buttonStop.isEnabled = false
                    buttonBroadcast.isEnabled = true
                    buttonBroadcast.visibility = View.VISIBLE
                    buttonAdvertise.visibility = View.GONE
                    buttonDiscover.visibility = View.GONE
                    buttonStop.visibility = View.GONE

                    Log.d("INFO_448_DEBUG", "Connection success")
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                    Log.d("INFO_448_DEBUG", "Connection rejected")
                }
                ConnectionsStatusCodes.STATUS_ERROR -> {
                    Log.d("INFO_448_DEBUG", "Error")
                }
            }// We're connected! Can now start sending and receiving data.
            // The connection was rejected by one or both sides.
            // The connection broke before it was able to be accepted.
            // Unknown status code
            Log.d("INFO_448_DEBUG", "Connected")
        }

        override fun onDisconnected(endpointId: String)
        {
            // We've been disconnected from this endpoint. No more data can be
            // sent or received.
            Log.d("INFO_448_DEBUG", "Disconnected")
        }
    }

    inner class ReceiveBytesPayloadListener : PayloadCallback()
    {
        var message = broadcastMessage
        override fun onPayloadReceived(endpointId: String, payload: Payload)
        {
            // This always gets the full data of the payload. Will be null if it's not a BYTES
            // payload. You can check the payload type with payload.getType().
            val receivedBytes = payload.asBytes()
            if (receivedBytes != null) {
                message = String(receivedBytes, Charsets.UTF_8)
            }
            Log.d("INFO_448_DEBUG", "Payload Received: " + message)
            //Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate)
        {
            // Bytes payloads are sent as a single chunk, so you'll receive a SUCCESS update immediately
            // after the call to onPayloadReceived().
            if (update.status == PayloadTransferUpdate.Status.SUCCESS) {
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}


