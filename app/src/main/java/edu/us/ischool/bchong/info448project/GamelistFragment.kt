package edu.us.ischool.bchong.info448project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import org.json.JSONObject

private const val playmode = "PLAYMODE"
private const val useridentity = "IDENTITY"

class GamelistFragment : Fragment() {
    private var mode: String? = null
    private var identity: String? = null
    private lateinit var gamechoice: String
    private lateinit var startgamebtn: Button
    private val gamelistData: JSONObject = JSONObject(
        """{
        |"Single":{
        |   "NumberOfGame":"2",
        |   "GameName" : ["Shake the Soda", "Flip the Phone"]
        |},
        |"Multi" :{
        |   "NumberOfGame":"3",
        |   "GameName" : ["Shake the Soda", "Answer the Phone", "Roll the Dice"]
        |}}
    """.trimMargin()
    )
    private var listener: OnFragmentInteractionListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mode = it.getString(playmode)
            identity = it.getString(useridentity)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View? = null
        if (mode == "Single") {

            view = inflater.inflate(R.layout.fragment_singlegamelist, container, false)

            val games = gamelistData.getJSONObject(mode).getJSONArray("GameName")

            var game1sbtn = view.findViewById<Button>(R.id.buttongame1s)
            var game2sbtn = view.findViewById<Button>(R.id.buttongame2s)
            startgamebtn = view.findViewById<Button>(R.id.buttonsinglestart)
            startgamebtn.isEnabled = false

            Log.d("GameSystem", "I am in singlelayout")

            game1sbtn.setText(games[0].toString())
            game2sbtn.setText(games[1].toString())

            // Set up the current Answer
            game1sbtn.setOnClickListener() {
                gamechoice = game1sbtn.text.toString()
                startgamebtn.isEnabled = true
            }
            game2sbtn.setOnClickListener() {
                gamechoice = game2sbtn.text.toString()
                startgamebtn.isEnabled = true
            }
            startgamebtn.setOnClickListener() {
                val intent = Intent(activity, GameActivity::class.java)
                intent.putExtra("IDENTITY", identity)
                intent.putExtra("GAME", gamechoice)
                startActivity(intent)
            }


        } else {
            view = inflater.inflate(R.layout.fragment_multigamelist, container, false)

            val games = gamelistData.getJSONObject(mode).getJSONArray("GameName")

            var game1btn = view.findViewById<Button>(R.id.buttongame1)
            var game2btn = view.findViewById<Button>(R.id.buttongame2)
            var game3btn = view.findViewById<Button>(R.id.buttongame3)
            startgamebtn = view.findViewById<Button>(R.id.buttonmultistart)
            startgamebtn.isEnabled = false

            game1btn.setText(games[0].toString())
            game2btn.setText(games[1].toString())
            game3btn.setText(games[2].toString())

            // Set up the current Answer
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
                val intent = Intent(activity, GameActivity::class.java)
                intent.putExtra("IDENTITY", identity)
                intent.putExtra("GAME", gamechoice)
                startActivity(intent)
            }
        }
        return view
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

    interface OnFragmentInteractionListener {
        fun onGameSelect(gamemode: String, playeridentity: String) {}
    }

    companion object {
        @JvmStatic
        fun newInstance(gamemode: String, playeridentity: String) =
            GamelistFragment().apply {
                arguments = Bundle().apply {
                    putString(playmode, gamemode)
                    putString(useridentity,playeridentity)
                }
            }
    }
}
