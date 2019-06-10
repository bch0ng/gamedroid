package edu.us.ischool.bchong.info448project

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


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_ROOM_CODE = "roomCode"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [RoomLobbyFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [RoomLobbyFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class RoomLobbyFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var roomCode: String? = null

    private var listener: OnFragmentInteractionListener? = null

    private lateinit var roomCodeShow: TextView
    private lateinit var playersList: TextView
    private lateinit var closeButton: Button

    private lateinit var nearby: NearbyConnection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            roomCode = it.getString(ARG_ROOM_CODE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_room_lobby, container, false)
        handleView(view)
        return view
    }

    fun handleView(view: View) {
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

    val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
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
         * @param param2 Parameter 2.
         * @return A new instance of fragment RoomLobbyFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(roomCode: String) =
            RoomLobbyFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ROOM_CODE, roomCode)
                }
            }
    }
}
