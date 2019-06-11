package game

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
import edu.us.ischool.bchong.info448project.NearbyConnection
import edu.us.ischool.bchong.info448project.R
import android.app.Activity.RESULT_OK

private const val PLAYMODE = "PLAYMODE"
private const val IDENTITY = "IDENTITY"

class GamelistFragment : Fragment() {
    private var mode: String? = null
    private var useridentity: String? = null
    private lateinit var gamechoice: String
    private lateinit var startgamebtn: Button

    private var isBroadcastListenerActive: Boolean = false

    private var singlePlayerGameNames = arrayOf("Shake the Soda", "Flip the Phone")
    private var multiPlayerGameNames = arrayOf("Answer the Phone","RollTheDiceHost", "Roll the Dice")

    private var listener: OnGameInteractionListener? = null

    private lateinit var nearby: NearbyConnection

    private lateinit var gameHost: GameHost


    override fun onPause()
    {
        super.onPause()
        LocalBroadcastManager.getInstance(nearby.getContext()).unregisterReceiver(broadCastReceiver)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nearby = NearbyConnection.instance
        arguments?.let {
            mode = it.getString(PLAYMODE)
            useridentity = it.getString(IDENTITY)
        }
        nearby = NearbyConnection.instance
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View? = null

        view = inflater.inflate(R.layout.fragment_gamelist, container, false)
        var games:Array<String> ?= null
        if(mode == "Single"){
            games = singlePlayerGameNames
        } else{
            games = multiPlayerGameNames
        }

        var game1sbtn: Button = view.findViewById(R.id.buttongame1)
        var game2sbtn: Button = view.findViewById(R.id.buttongame2)

        startgamebtn = view.findViewById(R.id.buttonstart)
        startgamebtn.isEnabled = false

        game1sbtn.setText(games[0])
        game2sbtn.setText(games[1])
        Log.i("game","identity is $useridentity")
        if (useridentity == "Host") {
            game1sbtn.setOnClickListener() {
                gamechoice = game1sbtn.text.toString()
                startgamebtn.isEnabled = true
            }
            game2sbtn.setOnClickListener() {
                gamechoice = game2sbtn.text.toString()
                startgamebtn.isEnabled = true
            }

            startgamebtn.visibility = View.VISIBLE

            startgamebtn.setOnClickListener() {
                (activity as OnGameInteractionListener).onGameStart(gamechoice)
            }
        } else {
            startgamebtn.isEnabled = false
            startgamebtn.visibility = View.GONE
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnGameInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener")
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
            if (intent?.hasExtra("closeRoom")!!) {
                LocalBroadcastManager.getInstance(nearby.getContext()).unregisterReceiver(this)
                isBroadcastListenerActive = false
                val intent = Intent()
                    intent.putExtra("key_response", "closed")
                activity?.setResult(RESULT_OK, intent)
                activity?.finish()
            }
            if(intent!!.getStringExtra(":dice")!=null){
                (activity as GamelistFragment.OnGameInteractionListener).onGameStart("Roll the Dice")
            }
        }
    }

    override fun onResume()
    {
        super.onResume()
        if (!isBroadcastListenerActive) {
            LocalBroadcastManager.getInstance(nearby.getContext()).registerReceiver(broadCastReceiver,
                IntentFilter("edu.us.ischool.bchong.info448project.ACTION_SEND")
            )
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnGameInteractionListener {
        fun onGameStart(gamechoice: String) {

        }
    }

    companion object {
        @JvmStatic
        fun newInstance(playmode: String, identity: String) =
            GamelistFragment().apply {
                arguments = Bundle().apply {
                    putString(PLAYMODE, playmode)
                    putString(IDENTITY, identity)
                }
            }
    }
}
