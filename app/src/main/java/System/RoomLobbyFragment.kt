package System

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import edu.us.ischool.bchong.info448project.NearbyConnection
import edu.us.ischool.bchong.info448project.R

private const val ARG_ROOM_CODE = "roomCode"

/**
 * A simple [Fragment] subclass.
 * Use the [RoomLobbyFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class RoomLobbyFragment : Fragment()
{
    private var roomCode: String? = null

    private lateinit var roomCodeShow: TextView
    private lateinit var playersList: TextView
    private lateinit var closeButton: Button

    private lateinit var nearby: NearbyConnection

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        arguments?.let {
            roomCode = it.getString(ARG_ROOM_CODE)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View?
    {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_room_lobby, container, false)
        handleView(view)
        return view
    }

    /**
     * Generates the view for this fragment.
     *
     * @param view  view to generate on top of
     */
    private fun handleView(view: View)
    {
        nearby = NearbyConnection.instance

        roomCodeShow = view.findViewById(R.id.room_code_show)
        playersList = view.findViewById(R.id.players_list)
        closeButton = view.findViewById(R.id.close_button)

        roomCodeShow.text = roomCode

        if (nearby.isHosting()) {
            playersList.text = "${nearby.getMyUsername()} (You are the Host)"
        } else {
            val players = nearby.getCurrPlayers()
            playersList.text = ""
            for (player in players) {
                playersList.text = playersList.text.toString() + "\n" + player
            }
        }

        closeButton.setOnClickListener {
            fragmentManager?.popBackStack()
        }
    }

    /**
     * Listens to any broadcast messages.
     *
     * @note: If it receives a room code message, it will open the room lobby fragment.
     */
    private val broadCastReceiver = object : BroadcastReceiver()
    {
        override fun onReceive(contxt: Context?, intent: Intent?)
        {
            if (intent?.hasExtra("message")!!) {
                Log.d("INFO_448_DEBUG", "Broadcast message received: ${intent?.getStringExtra("message")}")
                val message = intent?.getStringExtra("message")
                if (message?.startsWith("updateRoom:")!!) {
                    val players = nearby.getCurrPlayers()
                    playersList.text = ""
                    for ((index, player) in players.withIndex()) {
                        if (index == 0 && nearby.isHosting()) {
                            playersList.text = "$player (You are the Host)"
                        } else {
                            playersList.text = playersList.text.toString() + "\n" + player
                        }
                    }
                }
                Toast.makeText(context, intent?.getStringExtra("message"), Toast.LENGTH_SHORT).show()
            } else if (intent.hasExtra("roomCode")) {
                Log.d("INFO_448_DEBUG", "Broadcast message received: ${intent?.getStringExtra("roomCode")}")
                if (roomCodeShow.text.toString().isBlank()) {
                    val message = intent?.getStringExtra("roomCode")
                    roomCodeShow.text = message
                }
            }
        }
    }

    override fun onResume()
    {
        super.onResume()
        LocalBroadcastManager.getInstance(nearby.getContext()).registerReceiver(broadCastReceiver,
            IntentFilter("edu.us.ischool.bchong.info448project.ACTION_SEND")
        )
    }

    override fun onPause()
    {
        super.onPause()
        nearby.stopDiscovery()
        nearby.stopAdvertising()
        LocalBroadcastManager.getInstance(nearby.getContext()).unregisterReceiver(broadCastReceiver)
    }

    companion object
    {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param roomCode  room code to display
         * @return A new instance of fragment RoomLobbyFragment.
         */
        @JvmStatic
        fun newInstance(roomCode: String) =
            RoomLobbyFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ROOM_CODE, roomCode)
                }
            }
    }
}
