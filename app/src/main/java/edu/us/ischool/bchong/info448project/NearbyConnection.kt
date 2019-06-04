package edu.us.ischool.bchong.info448project

import android.app.Application
import android.content.Intent
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*

const val USER_NICKNAME: String = "info448project"
const val SERVICE_ID_BASE: String = "edu.us.ischool.bchong.info448project_"
const val ROOM_CODE_LENGTH: Int = 4

object NearbyConnection: Application()
{
    private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    private var endpointIDList: ArrayList<String> = ArrayList()
    private var endpointIDUsernameScoreMap: MutableMap<String, Pair<String, String?>> = HashMap()
    private var broadcastMessage: String = ""

    init
    {
        Log.d("INFO_448_DEBUG", "Init complete")
    }

    fun getClient(): ConnectionsClient
    {
        return Nearby.getConnectionsClient(this@NearbyConnection)
    }

    fun startAdvertising(): String
    {
        val advertisingOptions = AdvertisingOptions.Builder().setStrategy(Strategy.P2P_STAR).build()

        val roomCode = (1..ROOM_CODE_LENGTH)
            .map { kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
        Log.d("INFO_448_DEBUG", roomCode)

        Nearby.getConnectionsClient(this@NearbyConnection)
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

    fun stopAdvertising()
    {
        Nearby.getConnectionsClient(this@NearbyConnection).stopAdvertising()
        Log.d("INFO_448_DEBUG", "Stopped Advertising")
    }

    fun startDiscovery(roomCode: String)
    {
        Log.d("INFO_448_DEBUG", roomCode)
        val discoveryOptions = DiscoveryOptions.Builder().setStrategy(Strategy.P2P_STAR).build()
        Nearby.getConnectionsClient(this@NearbyConnection)
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

    fun stopDiscovery()
    {
        Nearby.getConnectionsClient(this@NearbyConnection).stopDiscovery()
        Log.d("INFO_448_DEBUG", "Stopped Discovering")
    }

    fun sendMessage(endpointID: String, message: String)
    {
        val bytesPayload = Payload.fromBytes(broadcastMessage.toByteArray(Charsets.UTF_8))
        Nearby.getConnectionsClient(this@NearbyConnection).sendPayload(endpointID, bytesPayload)
    }

    fun sendMessageAll(message: String)
    {
        val bytesPayload = Payload.fromBytes(broadcastMessage.toByteArray(Charsets.UTF_8))
        Nearby.getConnectionsClient(this@NearbyConnection).sendPayload(endpointIDList, bytesPayload)
    }

    fun stopBroadcast() {
        val nearby = Nearby.getConnectionsClient(this@NearbyConnection)
        nearby.stopAdvertising()
        nearby.stopAllEndpoints()
        for (id in endpointIDList) {
            nearby.disconnectFromEndpoint(id)
        }
    }

    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback()
    {
        override fun onEndpointFound(endpointID: String, info: DiscoveredEndpointInfo)
        {
            // An endpoint was found. We request a connection to it.
            Log.d("INFO_448_DEBUG", "Endpoint Found")
            Nearby.getConnectionsClient(this@NearbyConnection)
                .requestConnection(USER_NICKNAME, endpointID, connectionLifecycleCallback)
                .addOnSuccessListener {
                    // We successfully requested a connection. Now both sides
                    // must accept before the connection is established.
                    endpointIDList.add(endpointID)
                    stopDiscovery()
                    Log.d("INFO_448_DEBUG", "Connection success (join)")
                }
                .addOnFailureListener { e: Exception ->
                    // Nearby Connections failed to request the connection.
                    Log.d("INFO_448_DEBUG", "Connection Failed\n" + e.message)
                }
        }

        override fun onEndpointLost(endpointID: String)
        {
            // A previously discovered endpoint has gone away.
            Log.d("INFO_448_DEBUG", "Endpoint Lost")
        }
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback()
    {
        override fun onConnectionInitiated(endpointID: String, connectionInfo: ConnectionInfo)
        {
            // Automatically accept the connection on both sides.
            Nearby.getConnectionsClient(this@NearbyConnection)
                .acceptConnection(endpointID, receivePayloadCallback)
        }

        override fun onConnectionResult(endpointID: String, result: ConnectionResolution)
        {
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    endpointIDList.add(endpointID)
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

        override fun onDisconnected(endpointID: String)
        {
            // We've been disconnected from this endpoint. No more data can be
            // sent or received.
            Log.d("INFO_448_DEBUG", "Disconnected")
        }
    }

    val receivePayloadCallback = object : PayloadCallback()
    {
        override fun onPayloadReceived(endpointID: String, payload: Payload)
        {
            // This always gets the full data of the payload. Will be null if it's not a BYTES
            // payload. You can check the payload type with payload.getType().
            val receivedBytes = payload.asBytes()
            var message = ""
            if (receivedBytes != null) {
                message = String(receivedBytes, Charsets.UTF_8)

                when {
                    message.substring(0, 9) == "username:" -> {
                        endpointIDUsernameScoreMap[endpointID] = Pair(message.substring(9, message.length), null)
                    }
                    message.substring(0, 6) == "score:" -> {
                        val value: Pair<String, String?>? = endpointIDUsernameScoreMap[endpointID]
                        endpointIDUsernameScoreMap[endpointID] = Pair(value!!.first, message.substring(6, message.length))
                    }
                    else -> {}
                }
                val intent = Intent()
                    intent.action = "edu.us.ischool.bchong.info448project"
                    intent.putExtra("message", message)
                sendBroadcast(intent)
            }
            Log.d("INFO_448_DEBUG", "Payload Received: $message")
            //Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
        }

        override fun onPayloadTransferUpdate(endpointID: String, update: PayloadTransferUpdate)
        {
            // Bytes payloads are sent as a single chunk, so you'll receive a SUCCESS update immediately
            // after the call to onPayloadReceived().
            if (update.status == PayloadTransferUpdate.Status.SUCCESS) {
                Log.d("INFO_448_DEBUG", "Payload successfully sent")
            }
        }
    }


}