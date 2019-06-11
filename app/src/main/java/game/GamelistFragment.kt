package game

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import edu.us.ischool.bchong.info448project.R
import org.json.JSONObject

private const val playmode = "PLAYMODE"
private const val identity = "IDENTITY"

class GamelistFragment : Fragment() {
    private var mode: String? = null
    private var useridentity: String? = null
    private lateinit var gamechoice: String
    private lateinit var startgamebtn: Button

    private var singlePlayerGameNames= arrayOf("Shake the Soda","Flip the Phone")
    private var multiPlayerGameNames= arrayOf("Shake the Soda", "Answer the Phone", "Roll the Dice")
    private var listener: OnGameInteractionListener? = null

    private var instructiondata : JSONObject = JSONObject("""{
        |"Shake the Soda" : "Shake the soda is a party game where players sit around in a circle and pass the phone around. When they get the phone, each player shakes it like a soda as many times as they want. If the soda explodes on you then you lose.",
        |"Flip the Phone" : "Flip the phone is a single player game promoting physical activity and fun! Our scoring systems rewards players for any kind of tricks and moves they preform with their phone. Flip it in your hand or play catch with your friends. The possibilities are endless.

",
        |"Answer the Phone" : "Answer the phone is a multi-player game where each player places their phones face down in front of them. When the phones ring, the first player to flip their phone over wins.",
        |"Roll the Dice" : "Roll the dice is a deception based party game for all your friends. Take turns rolling dice and making bets on who's the winner by feeling vibrations from your phone."
        |}""".trimMargin())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mode = it.getString(playmode)
            useridentity = it.getString(identity)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View? = null
        Log.e("game", "Should have view")
        if (mode == "Single") {

            view = inflater.inflate(R.layout.fragment_singlegamelist, container, false)

            //val games = gamelistData.getJSONObject(mode).getJSONArray("GameName")
            val games = singlePlayerGameNames
            var game1sbtn = view.findViewById<Button>(R.id.buttongame1s)
            var game2sbtn = view.findViewById<Button>(R.id.buttongame2s)
            val instruction = view.findViewById<TextView>(R.id.txtInstruction)

            startgamebtn = view.findViewById<Button>(R.id.buttonsinglestart)
            startgamebtn.isEnabled = false
            Log.e("game", "Should have view")
            game1sbtn.setText(games[0].toString())
            game2sbtn.setText(games[1].toString())

            game1sbtn.setOnClickListener() {
                gamechoice = game1sbtn.text.toString()
                startgamebtn.isEnabled = true
                var instruct = instructiondata.getString(gamechoice)
                instruction.setText(instruct)
//                (activity as GamelistFragment.OnGameInteractionListener).onInstruction(gamechoice)
            }
            game2sbtn.setOnClickListener() {
                gamechoice = game2sbtn.text.toString()
                startgamebtn.isEnabled = true
                var instruct = instructiondata.getString(gamechoice)
                instruction.setText(instruct)
//                (activity as GamelistFragment.OnGameInteractionListener).onInstruction(gamechoice)
            }

            startgamebtn.setOnClickListener() {
                (activity as GamelistFragment.OnGameInteractionListener).onGameStart(gamechoice)

            }



        } else {
            view = inflater.inflate(R.layout.fragment_multigamelist, container, false)

            //val games = gamelistData.getJSONObject(mode).getJSONArray("GameName")
            val games=multiPlayerGameNames
            var game1btn = view.findViewById<Button>(R.id.buttongame1)
            var game2btn = view.findViewById<Button>(R.id.buttongame2)
            var game3btn = view.findViewById<Button>(R.id.buttongame3)
            startgamebtn = view.findViewById<Button>(R.id.buttonmultistart)
            startgamebtn.isEnabled = false

            game1btn.setText(games[0].toString())
            game2btn.setText(games[1].toString())
            game3btn.setText(games[2].toString())

            game1btn.setOnClickListener() {
                gamechoice = game1btn.text.toString()
                startgamebtn.isEnabled = true

            }
            game2btn.setOnClickListener() {
                gamechoice = game2btn.text.toString()
                startgamebtn.isEnabled = true
            }
            game3btn.setOnClickListener() {
                gamechoice = game3btn.text.toString()
                startgamebtn.isEnabled = true
            }
            startgamebtn.setOnClickListener() {

                (activity as GamelistFragment.OnGameInteractionListener).onGameStart(gamechoice)
            }
        }
        return view
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnGameInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnGameInteractionListener {
        fun onGameStart(gamechoice:String){}
        fun onInstruction(gamechoice: String){}
    }

    companion object {
        @JvmStatic
        fun newInstance(PLAYMODE: String, IDENTITY: String) =
            GamelistFragment().apply {
                arguments = Bundle().apply {
                    putString(playmode, PLAYMODE)
                    putString(identity,IDENTITY)
                }
            }
    }
}
