package edu.us.ischool.bchong.info448project

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
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


private const val ARG_USERNAME = "username"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FindCreateRoomFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FindCreateRoomFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FindCreateRoomFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var username: String? = null
    private var listener: OnFragmentInteractionListener? = null

    private lateinit var hostButton: Button
    private lateinit var discoverButton: Button
    private lateinit var stopButton: Button
    private lateinit var roomCodeField: EditText

    private lateinit var nearby: NearbyConnection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_find_create_room, container, false)
        handleView(view)
        return view
    }

    fun handleView(view: View) {
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

    val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            Log.d("INFO_448_DEBUG", "Broadcast message received: ${intent?.getStringExtra("message")}")
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

    fun openRoomLobbyFragment(roomCode: String) {
        val roomLobbyFragment = RoomLobbyFragment.newInstance(roomCode)
        val transaction = fragmentManager!!.beginTransaction()
            transaction.replace(R.id.fragment_find_create_room, roomLobbyFragment)
            transaction.addToBackStack(null)
            transaction.commit()
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @return A new instance of fragment FindCreateRoomFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            FindCreateRoomFragment().apply {
                username = NearbyConnection.instance.getMyUsername()
            }
    }
}
