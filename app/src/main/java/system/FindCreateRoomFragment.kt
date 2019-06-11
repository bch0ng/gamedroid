package system

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import edu.us.ischool.bchong.info448project.NearbyConnection
import edu.us.ischool.bchong.info448project.R

/**
 * A simple [Fragment] subclass.
 * Use the [FindCreateRoomFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FindCreateRoomFragment : Fragment()
{
    private var username: String? = null

    private lateinit var hostButton: Button
    private lateinit var discoverButton: Button
    private lateinit var stopButton: Button
    private lateinit var roomCodeField: EditText

    private lateinit var nearby: NearbyConnection

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View?
    {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_find_create_room, container, false)
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

        hostButton = view.findViewById(R.id.host_button)
        discoverButton = view.findViewById(R.id.discover_button)
        stopButton = view.findViewById(R.id.stop_button)
        roomCodeField = view.findViewById(R.id.room_code_text)

        val separator: TextView = view.findViewById(R.id.seperator_word_or)

        var mode = "none"
        hostButton.setOnClickListener {
            mode = "hosting"
            val roomCode = nearby.startAdvertising()
            Log.d("INFO_448_DEBUG", "HELLO")
            openRoomLobbyFragment(roomCode)
            Log.d("INFO_448_DEBUG", "BYE")
            discoverButton.isEnabled = false
            discoverButton.visibility = View.GONE
            hostButton.isEnabled = false
            stopButton.visibility = View.VISIBLE
            stopButton.isEnabled = true
            roomCodeField.visibility = View.GONE
        }
        roomCodeField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                discoverButton.isEnabled = (s.toString().length == 4)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        discoverButton.setOnClickListener {
            mode = "finding"
            nearby.startDiscovery(roomCodeField.text.toString())
            discoverButton.isEnabled = false
            hostButton.isEnabled = false
            hostButton.visibility = View.GONE
            separator.visibility = View.GONE
            stopButton.visibility = View.VISIBLE
            stopButton.isEnabled = true
            roomCodeField.isEnabled = false
        }
        stopButton.setOnClickListener {
            if (mode == "hosting")
                nearby.stopAdvertising()
            else if (mode == "finding")
                nearby.stopDiscovery()
            nearby.disconnectEndpointsAndStop()
            mode = "none"
            hostButton.isEnabled = true
            separator.visibility = View.VISIBLE
            discoverButton.isEnabled = true
            discoverButton.visibility = View.VISIBLE
            stopButton.visibility = View.GONE
            stopButton.isEnabled = false
            hostButton.visibility = View.VISIBLE
            roomCodeField.visibility = View.VISIBLE
            roomCodeField.isEnabled = true
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
            Log.d("INFO_448_DEBUG", "Broadcast message received:" +
                    "${intent?.getStringExtra("message")}")
            if (intent?.hasExtra("roomCode")!!) {
                openRoomLobbyFragment(intent.getStringExtra("roomCode"))
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
        LocalBroadcastManager.getInstance(nearby.getContext()).unregisterReceiver(broadCastReceiver)
    }

    /**
     * Opens the room lobby fragment and displays the room code.
     *
     * @param roomCode  room code to display on room lobby
     */
    fun openRoomLobbyFragment(roomCode: String)
    {
        val roomLobbyFragment = RoomLobbyFragment.newInstance(roomCode)
        val transaction = fragmentManager!!.beginTransaction()
            transaction.replace(R.id.fragment_find_create_room, roomLobbyFragment)
            transaction.addToBackStack(null)
            transaction.commit()
    }

    companion object
    {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment FindCreateRoomFragment.
         */
        @JvmStatic
        fun newInstance() =
            FindCreateRoomFragment().apply {
                username = NearbyConnection.instance.getMyUsername()
            }
    }
}
