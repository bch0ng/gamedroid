package system

import android.app.Activity.RESULT_OK
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
import android.widget.*
import edu.us.ischool.bchong.info448project.NearbyConnection
import edu.us.ischool.bchong.info448project.R
import org.w3c.dom.Text

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

    private var isBroadcastListenerActive: Boolean = false

    private var isGameListOpen: Boolean = false
    private lateinit var playersContainer: LinearLayout
    private var mView: View? = null

    private lateinit var nearby: NearbyConnection

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        arguments?.let {
            roomCode = it.getString(ARG_ROOM_CODE)
        }
        nearby = NearbyConnection.instance
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View?
    {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_room_lobby, container, false)
        mView = view
        handleView(view)
        return view
    }

    private fun addPlayerToPlayersContainer(view: View, playerName: String, identity: String)
    {
        val playerLinearLayout: LinearLayout = layoutInflater.inflate(R.layout.gameroom_player, null) as LinearLayout
            playerLinearLayout.findViewById<TextView>(R.id.player_name_text_view).text = playerName
        if (identity == "Host+Me") {
            playerLinearLayout.findViewById<ImageView>(R.id.user_identity).visibility = View.VISIBLE
            playerLinearLayout.findViewById<ImageView>(R.id.user_identity_extra).visibility = View.VISIBLE
        } else if (identity == "Host") {
            playerLinearLayout.findViewById<ImageView>(R.id.user_identity).visibility = View.GONE
            playerLinearLayout.findViewById<ImageView>(R.id.user_identity_extra).visibility = View.VISIBLE
        } else if (identity == "Me") {
            val userIdentityImageView = playerLinearLayout.findViewById<ImageView>(R.id.user_identity)
                userIdentityImageView.visibility = View.VISIBLE
                userIdentityImageView.setImageResource(R.drawable.baseline_person_pin_circle_white_36)
        }
        playersContainer.addView(playerLinearLayout)
    }

    /**
     * Generates the view for this fragment.
     *
     * @param view  view to generate on top of
     */
    private fun handleView(view: View)
    {
        roomCodeShow = view.findViewById(R.id.room_code_show)
        //playersList = view.findViewById(R.id.players_list)
        startButton = view.findViewById(R.id.start_button)
        playersContainer = view.findViewById(R.id.players_container)

        roomCodeShow.text = roomCode

        if (nearby.isHosting()) {
            addPlayerToPlayersContainer(view, nearby.getMyUsername(), "Host+Me")
        } else {
            val players = nearby.getCurrPlayers()
            playersContainer.removeAllViews()
            for (player in players) {
                addPlayerToPlayersContainer(view, player, "None")
            }
        }

        startButton.setOnClickListener {
            nearby.sendMessageAll("openGameList:true")
            val intent = Intent(activity, GameActivity::class.java)
                intent.putExtra("IDENTITY", "Host")
                intent.putExtra("GAMEMODE","Multi")
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            isGameListOpen = true
        }
    }

    override fun onResume() {
        super.onResume()
        /*
         * Keeps the broadcast listener open even if on GameActivity, so that
         * players can still join the room.
         */
        if (!isBroadcastListenerActive) {
            LocalBroadcastManager.getInstance(nearby.getContext()).registerReceiver(
                broadCastReceiver,
                IntentFilter("edu.us.ischool.bchong.info448project.ACTION_SEND")
            )
            isBroadcastListenerActive = true
        }
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
                Log.d("INFO_448_DEBUG", "Broadcast message received: " +
                        "${intent?.getStringExtra("message")}")
                val message = intent?.getStringExtra("message")
                if (message?.startsWith("updateRoom:")!!) {
                    val players = nearby.getCurrPlayers()
                    playersContainer.removeAllViews()
                    for ((index, player) in players.withIndex()) {
                        if (index == 0 && nearby.isHosting()) {
                            addPlayerToPlayersContainer(mView!!, nearby.getMyUsername(), "Host+Me")
                        } else if (index == 0 && !nearby.isHosting()) {
                            addPlayerToPlayersContainer(mView!!, player, "Host")
                        } else if (index == 1 && !nearby.isHosting()) {
                            addPlayerToPlayersContainer(mView!!, nearby.getMyUsername(), "Me")
                        } else {
                            addPlayerToPlayersContainer(mView!!, player, "None")
                        }
                    }
                    if (nearby.isHosting() && nearby.getCurrPlayers().size > 1
                            && !startButton.isEnabled) {
                        startButton.isEnabled = true
                        startButton.visibility = View.VISIBLE
                    } else if (!nearby.isHosting()) {
                        startButton.isEnabled = false
                        startButton.visibility = View.GONE
                    }
                }
                /* Make the newly connected player on the same activity as the rest of the room */
                if (isGameListOpen) {
                    nearby.sendMessageAll("openGameList:true")
                }
                Toast.makeText(context, intent?.getStringExtra("message"), Toast.LENGTH_SHORT).show()
            } else if (intent.hasExtra("roomCode")) {
                Log.d("INFO_448_DEBUG", "Broadcast message received: " +
                        "${intent?.getStringExtra("roomCode")}")
                if (roomCodeShow.text.toString().isBlank()) {
                    val message = intent?.getStringExtra("roomCode")
                    roomCodeShow.text = message
                }
            } else if (intent.hasExtra("openGameList")) {
                if (!isGameListOpen) {
                    LocalBroadcastManager.getInstance(nearby.getContext()).unregisterReceiver(this)
                    isBroadcastListenerActive = false
                    val intent = Intent(context, GameActivity::class.java)
                    intent.putExtra("IDENTITY", "Guest")
                    intent.putExtra("GAMEMODE", "Multi")
                    startActivityForResult(intent, 0)
                    activity?.overridePendingTransition(R.anim.pop_in_fade, R.anim.pop_out_fade)
                    isGameListOpen = true
                }
            } else if (intent.hasExtra("closeRoom")) {
                closeRoomLobbyFragment()
            }
        }
    }

    /**
     * Closes the room lobby if returning from a finished game activity
     * (including game list fragment) since that means that the host has
     * closed the room.
     *
     * @param requestCode   code specified in startActivityForResult
     * @param resultCode    status of result from GameActivity
     * @param data          data returned from GameActivity
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent)
    {
        Log.d("INFO_448_DEBUG", "RETURNED FROM GAME LIST")
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                if (data.hasExtra("key_response")) {
                    if (data.getStringExtra("key_response") == "closed") {
                        isGameListOpen = false
                        closeRoomLobbyFragment()
                    }
                }
            }
        }
    }

    /**
     * Closes room lobby and makes a Toast explaining that the host
     * closed the room.
     */
    fun closeRoomLobbyFragment()
    {
        LocalBroadcastManager.getInstance(nearby.getContext())
                .unregisterReceiver(broadCastReceiver)
        isBroadcastListenerActive = false
        fragmentManager!!.popBackStack()
        Toast.makeText(activity, "Host closed the room.", Toast.LENGTH_SHORT).show()
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
