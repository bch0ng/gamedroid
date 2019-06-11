package edu.us.ischool.bchong.info448project

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import org.json.JSONObject
import java.io.Serializable
import java.util.concurrent.atomic.AtomicBoolean

private const val ROOM_ID_BASE: String = "edu.us.ischool.bchong.info448project_"
private const val ROOM_CODE_LENGTH: Int = 4
private const val USERNAME_NOT_SET_STRING: String = "username_not_set"
private const val ROOM_CODE_NOT_SET_STRING: String = "room_code_not_set"
private val CONNECTION_STRATEGY: Strategy = Strategy.P2P_STAR

/**
 * A wrapper class for Google's Nearby Connections API
 * that takes a context to call the Nearby Connections client.
 *
 * @param context   context to call Nearby Connections client
 */
class NearbyConnection private constructor(context: Context)
{
    private var endpointIDUsernameScoreMap: MutableMap<String, Pair<String, String?>>
            = HashMap() // Key: EndpointID, Value: <Username, Score>
    private val context: Context = context
    private var isHosting: Boolean = false
    private var username: String = USERNAME_NOT_SET_STRING
    private var roomCode: String = ROOM_CODE_NOT_SET_STRING

    private var players: ArrayList<String> = ArrayList() // [0] is host, [1] is curr player if not host

    companion object {
        private lateinit var INSTANCE: NearbyConnection
        private val initialized = AtomicBoolean()

        val instance: NearbyConnection get() = INSTANCE

        fun initialize(context: Context) {
            if(!initialized.getAndSet(true)) {
                INSTANCE = NearbyConnection(context)
            }
        }
    }

    /**
     * Returns the current user's name
     *
     * @return username string
     */
    fun getMyUsername(): String
    {
        return username
    }

    /**
     * Returns whether the current user is the host of the game lobby.
     *
     * @return true if advertising,
     *         false otherwise
     */
    fun isHosting(): Boolean
    {
        return isHosting
    }

    /**
     * Returns the context the connection is based off of.
     *
     * @return context
     */
    fun getContext(): Context
    {
        return context
    }

    /**
     * Returns the Nearby Connections client
     *
     * @return connection client
     */
    fun getClient(): ConnectionsClient
    {
        return Nearby.getConnectionsClient(context)
    }

    /**
     * Sets the current user's name
     *
     * @param newUsername   user's new name
     */
    fun setUsername(newUsername: String)
    {
        username = newUsername
    }

    /**
     * Creates a room and advertises it to all nearby players and sets the current
     * user as the host. Then returns the generated room code to enter.
     *
     * @return room code
     */
    fun startAdvertising(): String
    {
        players.add(username)
        isHosting = true
        val advertisingOptions =
                AdvertisingOptions.Builder().setStrategy(CONNECTION_STRATEGY).build()
        roomCode = (1..ROOM_CODE_LENGTH)
            .map { kotlin.random.Random.nextInt(0, 9) }
            .joinToString("")
        Log.d("INFO_448_DEBUG", roomCode)
        Nearby.getConnectionsClient(context)
            .startAdvertising(
                username, ROOM_ID_BASE + roomCode, connectionLifecycleCallback, advertisingOptions
            )
            .addOnSuccessListener { unused: Void? ->
                Log.d("INFO_448_DEBUG", "Advertising!")
            }
            .addOnFailureListener { e: Exception ->
                Log.d("INFO_448_DEBUG", "Not Advertising\n" + e.message)
            }
        return roomCode
    }

    /**
     * Searches for all nearby room with given room code.
     *
     * @param roomCode  room code to enter room
     */
    fun startDiscovery(roomCode: String)
    {
        Log.d("INFO_448_DEBUG", roomCode)
        val discoveryOptions =
                DiscoveryOptions.Builder().setStrategy(CONNECTION_STRATEGY).build()
        Nearby.getConnectionsClient(context)
            .startDiscovery(ROOM_ID_BASE + roomCode, endpointDiscoveryCallback, discoveryOptions)
            .addOnSuccessListener {
                Log.d("INFO_448_DEBUG", "Discovering")
            }
            .addOnFailureListener { e: Exception ->
                Log.d("INFO_448_DEBUG", "Not Discovering\n" + e.message)
            }
    }

    /**
     * Called after discovering a room with the room code.
     *
     * @note: Only for discovering users.
     */
    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback()
    {
        override fun onEndpointFound(endpointID: String, info: DiscoveredEndpointInfo)
        {
            Log.d("INFO_448_DEBUG", "Endpoint Found")
            Nearby.getConnectionsClient(context)
                .requestConnection(username, endpointID, connectionLifecycleCallback)
                .addOnSuccessListener {
                    stopDiscovery()
                    players.add(username)
                    Log.d("INFO_448_DEBUG", "Connection success (join)")
                }
                .addOnFailureListener { e: Exception ->
                    Log.d("INFO_448_DEBUG", "Connection Failed\n" + e.message)
                }
        }

        override fun onEndpointLost(endpointID: String)
        {
            Log.d("INFO_448_DEBUG", "Endpoint Lost")
        }
    }

    /**
     * Called after successfully connecting to a player or room.
     *
     * @note: For both advertising and discovering users.
     */
    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback()
    {
        override fun onConnectionInitiated(endpointID: String,
                                           connectionInfo: ConnectionInfo)
        {
            Nearby.getConnectionsClient(context)
                .acceptConnection(endpointID, receivePayloadCallback)
        }

        override fun onConnectionResult(endpointID: String, result: ConnectionResolution)
        {
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    Log.d("INFO_448_DEBUG", "Connection success")
                    endpointIDUsernameScoreMap[endpointID] = Pair(endpointID, null)
                    if (isHosting) {
                        sendMessageAll("roomCode:$roomCode")
                    } else {
                        sendMessageAll("addPlayer:$username")
                    }
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                    Log.d("INFO_448_DEBUG", "Connection rejected")
                }
                ConnectionsStatusCodes.STATUS_ERROR -> {
                    Log.d("INFO_448_DEBUG", "Error")
                }
            }
            Log.d("INFO_448_DEBUG", "Connected")
        }

        override fun onDisconnected(endpointID: String)
        {
            if (!isHosting) {
                val intent = Intent()
                    intent.action = "edu.us.ischool.bchong.info448project.ACTION_SEND"
                    intent.putExtra("closeRoom", "true")
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
            }
            Log.d("INFO_448_DEBUG", "Disconnected")
        }
    }

    /**
     * Stops advertising the room to other players.
     */
    fun stopAdvertising()
    {
        Nearby.getConnectionsClient(context).stopAdvertising()
        isHosting = false
        Log.d("INFO_448_DEBUG", "Stopped Advertising")
    }

    /**
     * Stop searching for a nearby room.
     */
    fun stopDiscovery()
    {
        Nearby.getConnectionsClient(context).stopDiscovery()
        Log.d("INFO_448_DEBUG", "Stopped Discovering")
    }

    /**
     * Returns a list of all current players in the game room.
     *
     * @return  list of all current players
     */
    fun getCurrPlayers(): ArrayList<String>
    {
        return players
    }

    /**
     * Send a message to a specific connected user using their endpointID.
     *
     * @param endpointID    other user to send a message
     * @param message       message string
     */
    fun sendMessage(endpointID: String, message: String)
    {
        val bytesPayload = Payload.fromBytes(message.toByteArray(Charsets.UTF_8))
        Nearby.getConnectionsClient(context).sendPayload(endpointID, bytesPayload)
    }

    /**
     * Send a message to all connected users using their endpointID.
     *
     * @param message   message string
     */
    fun sendMessageAll(message: String)
    {
        val bytesPayload = Payload.fromBytes(message.toByteArray(Charsets.UTF_8))
        Nearby.getConnectionsClient(context)
                .sendPayload(endpointIDUsernameScoreMap.keys.toList(), bytesPayload)
    }

    /**
     * Called after receiving a message from a user through sendMessage()
     * or sendMessageAll().
     */
    val receivePayloadCallback = object : PayloadCallback()
    {
        /**
         * Message received.
         *
         * @param endpointID    user who sent the message
         * @param payload       message payload
         */
        override fun onPayloadReceived(endpointID: String, payload: Payload)
        {
            val receivedBytes = payload.asBytes()
            var message = ""
            if (receivedBytes != null) {
                message = String(receivedBytes, Charsets.UTF_8)

                when {
                    /**
                     * Tells the currently displayed activity to open the game list
                     * activity (if it hasn't already).
                     *
                     * @note Only the non-host players will receive these messages.
                     */
                    message.startsWith("openGameList:") -> {
                        val intent = Intent()
                            intent.action = "edu.us.ischool.bchong.info448project.ACTION_SEND"
                            intent.putExtra("openGameList", "true")
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
                    }
                    /**
                     * Tells the currently displayed activity to open the game lobby
                     * activity (if it hasn't already) and display the passed room code.
                     *
                     * @note Only the non-host players will receive these messages.
                     */
                    message.startsWith("roomCode:") -> {
                        val intent = Intent()
                            intent.action = "edu.us.ischool.bchong.info448project.ACTION_SEND"
                            intent.putExtra("roomCode", message.substring(9))
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
                    }
                    /**
                     * Tells the currently displayed activity to update
                     * their room information.
                     *
                     * @note Only the non-host players will receive these messages.
                     */
                    message.startsWith("updateRoom:") -> {
                        players = ArrayList(message.substring(11)
                            .split(",").toList())
                        if (players[1] != username) {
                            players[players.indexOf(username)] = players[1]
                            players[1] = username
                        }
                        Log.d("INFO_448_DEBUG", "UPDATE ROOM: $message")
                        val intent = Intent()
                            intent.action = "edu.us.ischool.bchong.info448project.ACTION_SEND"
                            intent.putExtra("message", "updateRoom:true")
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
                    }
                    /**
                     * Adds a player's username to the players list and tells the
                     * currently displayed activity to update their room information
                     *
                     * @note: Only the host will receive these messages.
                     */
                    message.startsWith("addPlayer:") -> {
                        var playerUsername = message.substring(10)
                        Log.d("INFO_448_DEBUG", "Message to ADD PLAYER")
                        players.add(playerUsername)
                        if (isHosting) {
                            sendMessageAll("updateRoom:${players.joinToString(separator = ",") { it }}")
                        }
                        val intent = Intent()
                            intent.action = "edu.us.ischool.bchong.info448project.ACTION_SEND"
                            intent.putExtra("message", "updateRoom:true")
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
                    }
                    /**
                     * Removes a player from the players list and tells the currently
                     * displayed activity to update their room information
                     *
                     * @note: Only the host will receive these messages.
                     */
                    message.startsWith("removePlayer:") -> {
                        val playerUsername = message.substring(13)
                        Log.d("INFO_448_DEBUG", "REMOVING PLAYER: $playerUsername")
                        Log.d("INFO_448_DEBUG", players.toString())
                        Log.d("INFO_448_DEBUG", players.contains(playerUsername).toString())
                        if (players.contains(playerUsername)) {
                            players.remove(playerUsername)
                            val intent = Intent()
                                intent.action = "edu.us.ischool.bchong.info448project.ACTION_SEND"
                                intent.putExtra("message", "updateRoom:true")
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
                        }
                    }
                    /**
                     * TODO?
                     */
                    message.startsWith("room:") -> {}
                    /**
                     * TODO?
                     */
                    message.startsWith("dice:") -> {
                        Log.d("INFO_448_DEBUG", "Message starts with 'dice:'")
                        val jsonStr=message.substring(5)
                        var jsonObj=JSONObject(jsonStr)
                        var intent=Intent()
                        val keyset=jsonObj.keys()
                        while(keyset.hasNext()){
                            val value=keyset.next()
                            intent.putExtra(value,jsonObj.get(value) as Serializable)
                        }
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
                    }
                        message.startsWith("score:") -> {
                        Log.d("INFO_448_DEBUG", "Message starts with 'score:'")
                        val value: Pair<String, String?>? = endpointIDUsernameScoreMap[endpointID]
                        endpointIDUsernameScoreMap[endpointID] =
                            Pair(value!!.first,message.substring(6))
                    }
                    /**
                     * @note: JUST FOR TESTING; PLEASE REMOVE FOR PRODUCTION
                     */
                    message.startsWith("test:") -> {
                        Log.d("INFO_448_DEBUG", "Message starts with 'test:'")
                        val intent = Intent()
                        intent.action = "edu.us.ischool.bchong.info448project.ACTION_SEND"
                        intent.putExtra("message", "HELLO MARS")
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
                    }

                    message.startsWith("telephone: ") -> {
                        val intent = Intent()
                        intent.action = "edu.us.ischool.bchong.info448project.ACTION_SEND"
                        intent.putExtra("TELEPHONE_RING", message)
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
                    }

                    message.startsWith("gamechoice:") -> {
                        val intent = Intent()
                        intent.putExtra("GAME_CHOICE", message.substring(12))
                        intent.action = "edu.us.ischool.bchong.info448project.ACTION_SEND"
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
                    }
                    else -> {}
                }
            }
            Log.d("INFO_448_DEBUG", "Payload Received: $message")
        }

        /**
         * Checks the status of receiving of a message.
         *
         * @note: Bytes payloads are sent as a single chunk, so you'll receive a
         *        SUCCESS update immediately after the call to onPayloadReceived().
         *
         * @param endpointID    user who sent the message
         * @param update        status of receiving
         */
        override fun onPayloadTransferUpdate(endpointID: String, update: PayloadTransferUpdate)
        {
            if (update.status == PayloadTransferUpdate.Status.SUCCESS) {
                Log.d("INFO_448_DEBUG", "Payload successfully sent")
            }
        }
    }

    /**
     * Disconnect from all connected users and stop advertising/discovering.
     */
    fun disconnectEndpointsAndStop() {
        Log.d("INFO_448_DEBUG", "Remove player in disconnect")
        if (!isHosting) {
            Log.d("INFO_448_DEBUG", "Player not hosting, so sending remove request")
            sendMessageAll("removePlayer:$username")
        }
        players.clear()
        val nearby = Nearby.getConnectionsClient(context)
        nearby.stopAdvertising()
        nearby.stopDiscovery()
        nearby.stopAllEndpoints()
        for (id in endpointIDUsernameScoreMap.keys.toList()) {
            nearby.disconnectFromEndpoint(id)
        }
    }
}