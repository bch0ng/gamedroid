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
import android.graphics.Color
import android.widget.TextView
import org.json.JSONObject

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

    private var instructiondata : JSONObject = JSONObject("""{
        |"Shake the Soda" : "Shake the soda is a party game where players sit around in a circle and pass the phone around. When they get the phone, each player shakes it like a soda as many times as they want. If the soda explodes on you then you lose.",
        |"Flip the Phone" : "Flip the phone is a single player game promoting physical activity and fun! Our scoring systems rewards players for any kind of tricks and moves they preform with their phone. Flip it in your hand or play catch with your friends. The possibilities are endless.
",
        |"Answer the Phone" : "Answer the phone is a multi-player game where each player places their phones face down in front of them. When the phones ring, the first player to flip their phone over wins.",
        |"Roll the Dice" : "Roll the dice is a deception based party game for all your friends. Take turns rolling dice and making bets on who's the winner by feeling vibrations from your phone."
        |}""".trimMargin())

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
        val instruction = view.findViewById<TextView>(R.id.txtInstruction)
        var games: Array<String>? = null
        if (mode == "Single") {
            games = singlePlayerGameNames
        } else {
            games = multiPlayerGameNames
        }
        var game1sbtn: Button = view.findViewById(R.id.buttongame1)
        var game2sbtn: Button = view.findViewById(R.id.buttongame2)
        //For both host and guests, the startgamebtn is default to invisible, while host has access to startgamebtn
        startgamebtn = view.findViewById(R.id.buttonstart)
        startgamebtn.isEnabled = false
        startgamebtn.visibility = View.GONE
        game1sbtn.setText(games[0])
        game2sbtn.setText(games[1])
        if (useridentity == "Host") {
            startgamebtn.visibility = View.VISIBLE
            game1sbtn.setOnClickListener() {
                gamechoice = game1sbtn.text.toString()
                game1sbtn.setTextColor(Color.parseColor("#bc660b"))
                game2sbtn.setTextColor(Color.parseColor("#ffffff"))
                startgamebtn.isEnabled = true
                startgamebtn.setTextColor(Color.parseColor("#001c63"))
                var instruct = instructiondata.getString(gamechoice)
                instruction.setText(instruct)
            }
            game2sbtn.setOnClickListener() {
                game1sbtn.setTextColor(Color.parseColor("#ffffff"))
                game2sbtn.setTextColor(Color.parseColor("#bc660b"))
                gamechoice = game2sbtn.text.toString()
                startgamebtn.isEnabled = true
                startgamebtn.setTextColor(Color.parseColor("#001c63"))
                var instruct = instructiondata.getString(gamechoice)
                instruction.setText(instruct)
            }
            startgamebtn.setOnClickListener() {
                (activity as OnGameInteractionListener).onGameStart(gamechoice)
            }
        } else {
            game1sbtn.setOnClickListener() {
                gamechoice = game1sbtn.text.toString()
                var instruct = instructiondata.getString(gamechoice)
                instruction.setText(instruct)
            }
            game2sbtn.setOnClickListener() {
                gamechoice = game2sbtn.text.toString()
                var instruct = instructiondata.getString(gamechoice)
                instruction.setText(instruct)
            }
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnGameInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(
                context.toString()
                        + " must implement OnFragmentInteractionListener"
            )
        }
    }

    /**
     * Listens to any broadcast messages.
     *
     * @note: If it receives a room code message, it will open the room lobby fragment.
     */
    private val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
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

    override fun onResume() {
        super.onResume()
        if (!isBroadcastListenerActive) {
            LocalBroadcastManager.getInstance(nearby.getContext()).registerReceiver(
                broadCastReceiver,
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
