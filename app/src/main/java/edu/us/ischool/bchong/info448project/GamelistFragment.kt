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


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val playmode = "param1"
private const val useridentity = "param2"
private lateinit var view: View

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [GamelistFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [GamelistFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class GamelistFragment : Fragment() {
    // TODO: Rename and change types of parameters
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
        Log.d("GameSystem", "I am in oncreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View? = null
        Log.d("GameSystem", "I am in oncreateview")
        // Inflate the layout for this fragment
        if (mode == "Single") {

            view = inflater.inflate(R.layout.fragment_singlegamelist, container, false)

            val games = gamelistData.getJSONObject(mode).getJSONArray("GameName")
            //val choicegroup : RadioGroup = view.findViewById(R.id.radioGroup)

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
                val intent = Intent(activity, Gametemple::class.java)
                intent.putExtra("IDENTITY", identity)
                intent.putExtra("GAME", gamechoice)
                startActivity(intent)
            }


        } else {
            view = inflater.inflate(R.layout.fragment_multigamelist, container, false)

            val games = gamelistData.getJSONObject(mode).getJSONArray("GameName")
            //val choicegroup : RadioGroup = view.findViewById(R.id.radioGroup)

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
                val intent = Intent(activity, Gametemple::class.java)
                intent.putExtra("IDENTITY", identity)
                intent.putExtra("GAME", gamechoice)
                startActivity(intent)
            }
        }
        Log.d("GameSystem", "I should be returned")
        //Different layout?

        return view
    }

    // TODO: Rename method, update argument and hook method into UI event
    /*fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }*/


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
        fun onGameSelect(param1: String, param2: String) {}
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GamelistFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GamelistFragment().apply {
                arguments = Bundle().apply {
                    putString(playmode, param1)
                    putString(useridentity, param2)
                }
            }
    }
}
