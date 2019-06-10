package Game

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import edu.us.ischool.bchong.info448project.R


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val USERNAME = "USERNAME"
private const val SCORE = "SCORE"
private const val GAME = "GAME"
private  const val  IDENTITY = "IDENTITY"
private const val MODE = "MODE"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ScoreBoardFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ScoreBoardFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ScoreBoardFragment : Fragment(), GamelistFragment.OnGameInteractionListener {
    // TODO: Rename and change types of parameters
    private var username: String? = null
    private var score: String? = null
    private var identity: String? = null
    private lateinit var game : String
    private var mode: String? = null
    private var listener: OnScoreboardInteractionListener? = null
    private lateinit var playmode: String
    private lateinit var winner: String
    private lateinit var restartButton: Button
    private lateinit var newGameButton: Button
    private lateinit var endGameButton:Button


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
        restartButton = view.findViewById(R.id.buttonRestart)
        newGameButton = view.findViewById(R.id.buttonNew)
        endGameButton = view.findViewById(R.id.buttonEnd)
        if (identity == "Host") {
            restartButton.isEnabled = true
            newGameButton.isEnabled = true
            endGameButton.isEnabled = true
            restartButton.visibility = View.VISIBLE
            newGameButton.visibility = View.VISIBLE
            endGameButton.visibility = View.VISIBLE
        } else {
            restartButton.isEnabled = false
            newGameButton.isEnabled = false
            endGameButton.isEnabled = false
            restartButton.visibility = View.GONE
            newGameButton.visibility = View.GONE
            endGameButton.visibility = View.GONE
        }

        restartButton.setOnClickListener {
            newGameButton.isEnabled = false
            endGameButton.isEnabled = false
            listener?.onGameStart(game)
        }

        newGameButton.setOnClickListener {
            restartButton.isEnabled = false
            endGameButton.isEnabled = false
            // What if the player wants to switch between single mode and multi mode?

           listener?.onGameSelect()
        }

        endGameButton.setOnClickListener{
            newGameButton.isEnabled = false
            restartButton.isEnabled = false
            listener?.onEndCycle()
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
        // TODO: Update argument type and name
        fun onEndCycle()

        fun onGameSelect()
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
