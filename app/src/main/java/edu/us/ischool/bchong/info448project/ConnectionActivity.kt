package edu.us.ischool.bchong.info448project

import android.os.Bundle
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
import com.google.android.gms.nearby.connection.*

const val USER_NICKNAME: String = "info448project"
const val SERVICE_ID_BASE: String = "edu.us.ischool.bchong.info448project_"
const val ROOM_CODE_LENGTH: Int = 4
private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

class Connection_activity : AppCompatActivity() {

    private lateinit var buttonAdvertise: Button
    private lateinit var buttonDiscover: Button
    private lateinit var buttonStop: Button
    private lateinit var buttonBroadcast: Button
    private lateinit var roomCodeField: EditText
    private lateinit var roomCodeShow: TextView

    private var endpointID: ArrayList<String> = ArrayList()
    private var broadcastMessage: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connection)

        buttonAdvertise = findViewById(R.id.buttonAdvertise)
        buttonDiscover = findViewById(R.id.buttonDiscover)
        buttonStop = findViewById(R.id.buttonStop)
        buttonBroadcast = findViewById(R.id.buttonBroadcast)
        roomCodeField = findViewById(R.id.roomCodeText)
        roomCodeShow = findViewById(R.id.roomCodeShow)

        var mode = "none"
        buttonAdvertise.setOnClickListener {
            mode = "advertising"
            val roomCode = startAdvertising()
            buttonDiscover.isEnabled = false
            buttonDiscover.visibility = View.GONE
            buttonAdvertise.isEnabled = false
            buttonStop.isEnabled = true
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
            startDiscovery(roomCodeField.text.toString())
            buttonDiscover.isEnabled = false
            buttonAdvertise.isEnabled = false
            buttonAdvertise.visibility = View.GONE
            buttonStop.isEnabled = true
            roomCodeField.isEnabled = false
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
            buttonBroadcast.isEnabled = false
            buttonBroadcast.visibility = View.GONE
            buttonStop.isEnabled = false
            buttonAdvertise.visibility = View.VISIBLE
            roomCodeField.visibility = View.VISIBLE
            roomCodeField.isEnabled = true
            roomCodeShow.visibility = View.GONE
        }
        buttonBroadcast.setOnClickListener {
            mode = "broadcast"
            broadcastMessage = "Hello, world!"
            val bytesPayload = Payload.fromBytes(broadcastMessage.toByteArray(Charsets.UTF_8))
            for (id in endpointID) {
                Nearby.getConnectionsClient(this@Connection_activity).sendPayload(id, bytesPayload)
            }
        }
    }

    private fun stopBroadcast() {
        val nearby = Nearby.getConnectionsClient(this@Connection_activity)
        nearby.stopAdvertising()
        nearby.stopAllEndpoints()
        for (id in endpointID) {
            nearby.disconnectFromEndpoint(id)
        }
    }

    private fun startAdvertising(): String {
        val advertisingOptions = AdvertisingOptions.Builder().setStrategy(Strategy.P2P_STAR).build()

        val roomCode = (1..ROOM_CODE_LENGTH)
            .map { kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
        Log.d("INFO_448_DEBUG", roomCode)

        Nearby.getConnectionsClient(this@Connection_activity)
            .startAdvertising(
                USER_NICKNAME, SERVICE_ID_BASE + roomCode, connectionLifecycleCallback, advertisingOptions
            )
            .addOnSuccessListener { unused: Void? ->
                // We're advertising!
                Log.d("INFO_448_DEBUG", "Advertising!")
            }
            .addOnFailureListener { e: Exception ->
                // We were unable to start advertising.
                Log.d("INFO_448_DEBUG", "Not Advertising\n" + e.message)
            }
        return roomCode
    }

    private fun stopAdvertising() {
        Nearby.getConnectionsClient(this@Connection_activity).stopAdvertising()
        Log.d("INFO_448_DEBUG", "Stopped Advertising")
    }

    private fun startDiscovery(roomCode: String) {
        Log.d("INFO_448_DEBUG", roomCode)
        val discoveryOptions = DiscoveryOptions.Builder().setStrategy(Strategy.P2P_STAR).build()
        Nearby.getConnectionsClient(this@Connection_activity)
            .startDiscovery(SERVICE_ID_BASE + roomCode, endpointDiscoveryCallback, discoveryOptions)
            .addOnSuccessListener {
                // We're discovering!
                Log.d("INFO_448_DEBUG", "Discovering")
            }
            .addOnFailureListener { e: Exception ->
                // We're unable to start discovering.
                Log.d("INFO_448_DEBUG", "Not Discovering\n" + e.message)
            }
    }

    private fun stopDiscovery() {
        Nearby.getConnectionsClient(this@Connection_activity).stopDiscovery()
        Log.d("INFO_448_DEBUG", "Stopped Discovering")
    }

    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            // An endpoint was found. We request a connection to it.
            Log.d("INFO_448_DEBUG", "Endpoint Found")
            Nearby.getConnectionsClient(this@Connection_activity)
                .requestConnection(USER_NICKNAME, endpointId, connectionLifecycleCallback)
                .addOnSuccessListener {
                    // We successfully requested a connection. Now both sides
                    // must accept before the connection is established.
                    endpointID.add(endpointId)
                    stopDiscovery()
                    buttonStop.isEnabled = true
                    buttonBroadcast.isEnabled = true
                    buttonBroadcast.visibility = View.VISIBLE
                    buttonAdvertise.visibility = View.GONE
                    buttonDiscover.visibility = View.GONE

                    Log.d("INFO_448_DEBUG", "Connection success (join)")
                }
                .addOnFailureListener { e: Exception ->
                    // Nearby Connections failed to request the connection.
                    Log.d("INFO_448_DEBUG", "Connection Failed\n" + e.message)
                }
        }

        override fun onEndpointLost(endpointId: String) {
            // A previously discovered endpoint has gone away.
            Log.d("INFO_448_DEBUG", "Endpoint Lost")
        }
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            // Automatically accept the connection on both sides.
            Nearby.getConnectionsClient(this@Connection_activity)
                .acceptConnection(endpointId, ReceiveBytesPayloadListener())
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    endpointID.add(endpointId)
                    buttonStop.isEnabled = true
                    buttonBroadcast.isEnabled = true
                    buttonBroadcast.visibility = View.VISIBLE
                    buttonAdvertise.visibility = View.GONE
                    buttonDiscover.visibility = View.GONE

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

        override fun onDisconnected(endpointId: String) {
            // We've been disconnected from this endpoint. No more data can be
            // sent or received.
            Log.d("INFO_448_DEBUG", "Disconnected")
        }
    }

    inner class ReceiveBytesPayloadListener : PayloadCallback() {
        var message = broadcastMessage
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            // This always gets the full data of the payload. Will be null if it's not a BYTES
            // payload. You can check the payload type with payload.getType().
            val receivedBytes = payload.asBytes()
            if (receivedBytes != null) {
                message = String(receivedBytes, Charsets.UTF_8)
            }
            Log.d("INFO_448_DEBUG", "Payload Received: " + message)
            //Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            // Bytes payloads are sent as a single chunk, so you'll receive a SUCCESS update immediately
            // after the call to onPayloadReceived().
            if (update.status == PayloadTransferUpdate.Status.SUCCESS) {
                Toast.makeText(this@Connection_activity, message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}