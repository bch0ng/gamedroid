package edu.us.ischool.bchong.info448project

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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
        buttonAdvertise.setOnClickListener {
            startAdvertising()
            buttonDiscover.isEnabled = false
        }
        buttonDiscover.setOnClickListener {
            startDiscovery()
            buttonAdvertise.isEnabled=false
        }
        buttonStop.setOnClickListener {
            buttonAdvertise.isEnabled = true
            buttonDiscover.isEnabled = true
        }
    }

    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            // An endpoint was found. We request a connection to it.
            Nearby.getConnectionsClient(this@MainActivity)
                .requestConnection("boya", endpointId, connectionLifecycleCallback)
                .addOnSuccessListener { unused: Void ->
                    // We successfully requested a connection. Now both sides
                    // must accept before the connection is established.
                    Toast.makeText(this@MainActivity,"Connection success",Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { e: Exception ->
                    // Nearby Connections failed to request the connection.
                    Toast.makeText(this@MainActivity,"Connection Failed",Toast.LENGTH_LONG).show()
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
                    Toast.makeText(this@MainActivity,"Status ok",Toast.LENGTH_LONG).show()
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                    Toast.makeText(this@MainActivity,"Connection rejected",Toast.LENGTH_LONG).show()
                }
                ConnectionsStatusCodes.STATUS_ERROR -> {
                    Toast.makeText(this@MainActivity,"Error",Toast.LENGTH_LONG).show()
                }
            }// We're connected! Can now start sending and receiving data.
            // The connection was rejected by one or both sides.
            // The connection broke before it was able to be accepted.
            // Unknown status code
            Toast.makeText(this@MainActivity,"Connected",Toast.LENGTH_LONG).show()
        }

        override fun onDisconnected(endpointId: String) {
            // We've been disconnected from this endpoint. No more data can be
            // sent or received.
            Toast.makeText(this@MainActivity,"Disconnected",Toast.LENGTH_LONG).show()
        }
    }

    private fun startAdvertising() {
        val advertisingOptions = AdvertisingOptions.Builder().setStrategy(P2P_CLUSTER).build()
        Nearby.getConnectionsClient(this@MainActivity)
            .startAdvertising(
                "boya", "1234", connectionLifecycleCallback, advertisingOptions
            )
            .addOnSuccessListener { unused: Void ->
                // We're advertising!
                Toast.makeText(this@MainActivity,"Advertised!",Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e: Exception ->
                // We were unable to start advertising.
                Toast.makeText(this@MainActivity,"Not Advertising",Toast.LENGTH_LONG).show()
            }
    }

    private fun startDiscovery() {
        val discoveryOptions = DiscoveryOptions.Builder().setStrategy(P2P_CLUSTER).build()
        Nearby.getConnectionsClient(this@MainActivity)
            .startDiscovery("1234", endpointDiscoveryCallback, discoveryOptions)
            .addOnSuccessListener { unused: Void ->
                // We're discovering!
                Toast.makeText(this@MainActivity,"Discovered",Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e: Exception ->
                // We're unable to start discovering.
                Toast.makeText(this@MainActivity,"Not Discovering",Toast.LENGTH_LONG).show()
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


