package game

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import edu.us.ischool.bchong.info448project.R

private const val USERNAME = "USERNAME"
private const val SCORE = "SCORE"
private const val GAME = "GAME"
private  const val  IDENTITY = "IDENTITY"
private const val MODE = "MODE"
class ScoreBoardFragment : Fragment(), GamelistFragment.OnGameInteractionListener {

    private var username: String? = null
    private var score: String? = null
    private lateinit var identity: String
    private lateinit var game : String
    private lateinit var mode: String
    private var listener: OnScoreboardInteractionListener? = null
    private lateinit var winner: String
    private lateinit var restartButton: Button
    private lateinit var newGameButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            username = it.getString(USERNAME)
            score = it.getString(SCORE)
            game = it.getString(GAME)
            mode = it.getString(MODE)
            identity = it.getString(IDENTITY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_score_board, container, false)
        //TODO: SCOREBOARD DISPLAY
        var list = view.findViewById<RecyclerView>(R.id.recyclerView)



        restartButton = view.findViewById(R.id.buttonRestart)
        newGameButton = view.findViewById(R.id.buttonNew)
        restartButton.isEnabled=false
        newGameButton.isEnabled=false
        restartButton.visibility = View.GONE
        newGameButton.visibility = View.GONE

        if (identity == "Host") {
            restartButton.isEnabled = true
            newGameButton.isEnabled = true
            restartButton.visibility = View.VISIBLE
            newGameButton.visibility = View.VISIBLE
        }

        restartButton.setOnClickListener {
            newGameButton.isEnabled = false
            listener?.onGameStart(game)
        }

        newGameButton.setOnClickListener {
            restartButton.isEnabled = false
           listener?.onGameSelect(mode,identity)
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnScoreboardInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnScoreboardInteractionListener {
        fun onGameSelect(playmode: String, useridentity: String)
        fun onGameStart(gamechoice: String)

    }

    companion object {
        @JvmStatic
        fun newInstance(username: String, useridentity: String, gamechoice: String,userscore:String,playmode:String) =
            ScoreBoardFragment().apply {
                arguments = Bundle().apply {
                    putString(USERNAME, username)
                    putString(IDENTITY,useridentity)
                    putString(SCORE, userscore)
                    putString(GAME,gamechoice)
                    putString(MODE,playmode)
                }
            }
    }
}
