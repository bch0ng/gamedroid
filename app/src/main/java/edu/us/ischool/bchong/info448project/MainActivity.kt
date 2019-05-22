package edu.us.ischool.bchong.info448project

import android.Manifest
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.Strategy.P2P_CLUSTER
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback


class MainActivity : AppCompatActivity() {
    private lateinit var buttonAdvertise: Button
    private  lateinit var buttonDiscover : Button
    private  lateinit var buttonStop : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonAdvertise = findViewById(R.id.button)
        buttonDiscover = findViewById(R.id.button2)
        buttonStop = findViewById(R.id.button3)
        var mode = "none"
        buttonAdvertise.setOnClickListener {
            mode = "advertising"
            startAdvertising()
            buttonDiscover.isEnabled = false
            buttonAdvertise.isEnabled = false
            buttonStop.isEnabled = true
        }
        buttonDiscover.setOnClickListener {
            mode = "discovery"
            startDiscovery()
            buttonDiscover.isEnabled = false
            buttonAdvertise.isEnabled = false
            buttonStop.isEnabled = true
        }
        buttonStop.setOnClickListener {
            if (mode == "advertising")
                stopAdvertising()
            else if (mode == "discovery")
                stopDiscovery()
            mode = "none"
            buttonAdvertise.isEnabled = true
            buttonDiscover.isEnabled = true
        }
    }

    private fun startAdvertising() {
        val advertisingOptions = AdvertisingOptions.Builder().setStrategy(P2P_CLUSTER).build()

        Nearby.getConnectionsClient(this@MainActivity)
            .startAdvertising(
                "info448project", "edu.us.ischool.bchong.info448project", connectionLifecycleCallback, advertisingOptions
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
    private fun stopAdvertising() {
        Nearby.getConnectionsClient(this@MainActivity).stopAdvertising()
        Log.d("INFO_448_DEBUG", "Stopped Advertising")
    }

    private fun startDiscovery() {
        // Only one of these permissions is required (Fine location for Q and upwards)
        if (ContextCompat.checkSelfPermission(this@MainActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@MainActivity,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                8034)
        } else if (ContextCompat.checkSelfPermission(this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@MainActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                8035)
        } else {
            val discoveryOptions = DiscoveryOptions.Builder().setStrategy(P2P_CLUSTER).build()
            Nearby.getConnectionsClient(this@MainActivity)
                .startDiscovery("1234", endpointDiscoveryCallback, discoveryOptions)
                .addOnSuccessListener { unused: Void? ->
                    // We're discovering!
                    Log.d("INFO_448_DEBUG", "Discovered")
                }
                .addOnFailureListener { e: Exception ->
                    // We're unable to start discovering.
                    Log.d("INFO_448_DEBUG", "Not Discovering\n" + e.message)
                }
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            8034, 8035 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    val discoveryOptions = DiscoveryOptions.Builder().setStrategy(P2P_CLUSTER).build()
                    Nearby.getConnectionsClient(this@MainActivity)
                        .startDiscovery("1234", endpointDiscoveryCallback, discoveryOptions)
                        .addOnSuccessListener { unused: Void? ->
                            // We're discovering!
                            Log.d("INFO_448_DEBUG", "Discovered")
                        }
                        .addOnFailureListener { e: Exception ->
                            // We're unable to start discovering.
                            Log.d("INFO_448_DEBUG", "Not Discovering\n" + e.message)
                        }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }
    private fun stopDiscovery() {
        Nearby.getConnectionsClient(this@MainActivity).stopDiscovery()
        Log.d("INFO_448_DEBUG", "Stopped Discovering")
    }

    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            // An endpoint was found. We request a connection to it.
            Nearby.getConnectionsClient(this@MainActivity)
                .requestConnection("boya", endpointId, connectionLifecycleCallback)
                .addOnSuccessListener { unused: Void? ->
                    // We successfully requested a connection. Now both sides
                    // must accept before the connection is established.
                    Log.d("INFO_448_DEBUG", "Connection success")
                }
                .addOnFailureListener { e: Exception ->
                    // Nearby Connections failed to request the connection.
                    Log.d("INFO_448_DEBUG", "Connection Failed\n" + e.message)
                }
        }

        override fun onEndpointLost(endpointId: String) {
            // A previously discovered endpoint has gone away.
        }
    }


    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            val bytesPayload :Payload = Payload.fromBytes(byteArrayOf(0xa,0xb, 0xc,0xd))
            // Nearby.getConnectionsClient(this@MainActivity).sendPayload(toEndpointId, bytesPayload)
            // Automatically accept the connection on both sides.
            Nearby.getConnectionsClient(this@MainActivity).acceptConnection(endpointId, ReceiveBytesPayloadListener())
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    Log.d("INFO_448_DEBUG", "Status ok")
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

        override fun onDisconnected(endpointId: String) {
            // We've been disconnected from this endpoint. No more data can be
            // sent or received.
            Log.d("INFO_448_DEBUG", "Disconnected")
        }
    }

    internal class ReceiveBytesPayloadListener : PayloadCallback() {

        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            // This always gets the full data of the payload. Will be null if it's not a BYTES
            // payload. You can check the payload type with payload.getType().
            val receivedBytes = payload.asBytes()
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            // Bytes payloads are sent as a single chunk, so you'll receive a SUCCESS update immediately
            // after the call to onPayloadReceived().
        }
    }
}


