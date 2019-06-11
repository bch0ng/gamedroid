package system

import game.GameActivity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
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
    private lateinit var startButton: Button

    private lateinit var nearby: NearbyConnection

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        arguments?.let {
            roomCode = it.getString(ARG_ROOM_CODE)
        }

        nearby = NearbyConnection.instance

        LocalBroadcastManager.getInstance(nearby.getContext()).registerReceiver(broadCastReceiver,
            IntentFilter("edu.us.ischool.bchong.info448project.ACTION_SEND")
        )
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
        roomCodeShow = view.findViewById(R.id.room_code_show)
        playersList = view.findViewById(R.id.players_list)
        startButton = view.findViewById(R.id.start_button)
        //closeButton = view.findViewById(R.id.close_button)

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

        startButton.setOnClickListener {
            nearby.sendMessageAll("openGameList:true")
            val intent = Intent(activity, GameActivity::class.java)
                intent.putExtra("IDENTITY", "Host")
                intent.putExtra("GAMEMODE","Multi")
            startActivity(intent)
        }

        /*
        closeButton.setOnClickListener {
            fragmentManager?.popBackStack()
        }
        */
    }

    /**
     * Listens to any broadcast messages.
     *
     * @note: If it receives a room code message, it will open the room lobby fragment.
     */
    private val broadCastReceiver = object : BroadcastReceiver()
    {
        override fun onReceive(context: Context?, intent: Intent?)
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
                    if (nearby.isHosting() && nearby.getCurrPlayers().size > 1 && !startButton.isEnabled) {
                        startButton.isEnabled = true
                        startButton.visibility = View.VISIBLE
                    } else if (!nearby.isHosting()) {
                        startButton.isEnabled = false
                        startButton.visibility = View.GONE
                    }
                }
                Toast.makeText(context, intent?.getStringExtra("message"), Toast.LENGTH_SHORT).show()
            } else if (intent.hasExtra("roomCode")) {
                Log.d("INFO_448_DEBUG", "Broadcast message received: ${intent?.getStringExtra("roomCode")}")
                if (roomCodeShow.text.toString().isBlank()) {
                    val message = intent?.getStringExtra("roomCode")
                    roomCodeShow.text = message
                }
            } else if (intent.hasExtra("openGameList")) {
                LocalBroadcastManager.getInstance(nearby.getContext()).unregisterReceiver(this)
                val intent = Intent(activity, GameActivity::class.java)
                    intent.putExtra("IDENTITY", "Guest")
                    intent.putExtra("GAMEMODE","Multi")
                startActivity(intent)
            } else if (intent.hasExtra("closeRoom")) {
                fragmentManager?.popBackStack()
            }
        }
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
