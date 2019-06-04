package edu.us.ischool.bchong.info448project

import android.animation.ValueAnimator
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.net.Network
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import kotlinx.android.synthetic.main.dice.*
import kotlinx.android.synthetic.main.flip.*
import kotlinx.android.synthetic.main.postgame_buttons.*
import android.os.Build
import android.support.v7.content.res.AppCompatResources
import android.text.Layout
import android.util.Log
import android.widget.TextView
import java.util.*
import kotlin.math.roundToInt

class DiceFragment : Fragment(), GameFragment {

    override fun newInstance(game: Game): GameFragment {
        gameObj = game
        return this
    }

    // TODO: Rename and change types of parameters
    private var listener: OnFragmentInteractionListener? = null
    var gameObj: Game? = null
    lateinit var gyroscope: Sensor
    lateinit var accelerometer: Sensor
    //lateinit var linearAccelerometer:SensorManager
    lateinit var motionSensorController: SensorManager
    //lateinit var accelerometer:Sensor


    val PLAYER_KEY = "player"
    val PLAYERS_KEY = "players"
    val ID_SUFFIX = "Dice"
    lateinit var player: Pair<String, String>
    lateinit var players: Array<Pair<String, String>>
    private lateinit var playerDiceVisual: ImageView

    fun ShowWinner(winner: Pair<String, Int>) {
        val scoreString = "${winner.first} won with ${winner.second}"
        Log.v("dice", scoreString)
    }

    //Should be called by the client when the starting message is recieved from the server
    fun StartGame(myId: Pair<String, String>, allPlayers: Array<Pair<String, String>>) {
        Log.v("dice", "Start game called by server")
        var index = 0
        players = allPlayers
        /*allPlayers.map {
            players[index]=it
            index++
        }*/
        player = myId
        val fragMananager = fragmentManager
        val fragTransaction = fragMananager!!.beginTransaction()
        //val inflater=layoutInflater
        if (opponent_dice.childCount > 0) {
            opponent_dice.removeAllViews()
        }
        redrawPlayers()
        fragTransaction.commit()
    }


    private fun getIdString(id: String): String {
        return "$id$ID_SUFFIX"
    }

    private fun redrawPlayers() {
        players.map { thisPlayer: Pair<String, String> ->
            val newDiceObj = LayoutInflater.from(context).inflate(R.layout.dice_opponent, null)
            newDiceObj.tag = getIdString(thisPlayer.first)
            newDiceObj.findViewWithTag<TextView>("player_name").setText(thisPlayer.second)
            opponent_dice.addView(newDiceObj)
        }
    }

    private fun drawPlayers(savedInstanceState: Bundle) {
        val playerNameTag: TextView = player_dice.findViewWithTag("player_name") as TextView
        //val inflater=context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (savedInstanceState.containsKey(PLAYERS_KEY)) {
            players = savedInstanceState.get(PLAYERS_KEY) as Array<Pair<String, String>>
            redrawPlayers()
        }
        if (savedInstanceState.containsKey(PLAYER_KEY)) {
            player = savedInstanceState.get(PLAYER_KEY) as Pair<String, String>
            playerNameTag.text = player.second
        } else {
            playerNameTag.text = getString(R.string.default_player_name)
        }
        playerDiceVisual = player_dice.findViewWithTag("dice_img") as ImageView
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (players != null) {
            outState.putSerializable("players", players)
        }
        if (player != null) {
            outState.putSerializable("player", player)
        }
    }

    override fun onStart() {
        super.onStart()
        this.gameObj!!.onFragmentStart()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            drawPlayers(savedInstanceState)
        }
        restart_button.setOnClickListener {
            val ft = fragmentManager!!.beginTransaction()
            if (Build.VERSION.SDK_INT >= 26) {
                ft.setReorderingAllowed(false)
            }
            postgame_buttons.visibility = View.GONE
            ft.detach(this).attach(this).commit()
        }
        end_game_button.setOnClickListener {
            postgame_buttons.visibility = View.GONE
            activity!!.finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dice, container, false)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    /*
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }*/

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

    //Starts the rolling animation with the given force
    //Eat unit of force equals 1 radian of rotation per second
    fun diceRoll(force1: Float, force2: Float, currentRollEnergy: Double, duration: Long) {
        // TODO: test visuals
        val dimensionAnimator = ValueAnimator.ofFloat(0f, (Math.PI * duration).toFloat())
        dimensionAnimator.duration = duration
        dimensionAnimator.addUpdateListener(dimAnimator(player_dice, force1, force2))

    }

    //Changes the size of dice
    private fun dimAnimator(element: View, force1: Float, force2: Float) =
        object : ValueAnimator.AnimatorUpdateListener {
            override fun onAnimationUpdate(animation: ValueAnimator) {
                val animatedValue: Float = animation.animatedValue as Float
                element.layoutParams.height = (force1 * Math.cos(animatedValue.toDouble())).toInt()
                element.layoutParams.width = (force2 * Math.cos(animatedValue.toDouble())).toInt()
            }
        }


    fun revealRoll(id: String, rollValue: Int) {
        val parentElement = root_dice_layout.findViewWithTag<TextView>("${id}Dice")
        parentElement.findViewWithTag<TextView>("dice_text").text = "$rollValue"
    }

    //Do the visuals for when a opponents dice is rolled
    fun opponentRolled(id: String, strength: Double, duration: Long) {
        // TODO:
        try {
            val element: View = opponent_dice.findViewWithTag(getIdString(id))
            val dimensionAnimator = ValueAnimator.ofFloat(0f, (Math.PI * duration).toFloat())
            dimensionAnimator.duration = duration
            dimensionAnimator.addUpdateListener(
                dimAnimator(
                    element,
                    (strength * Math.random().roundToInt() * -1.0).toFloat(),
                    (strength * Math.random().roundToInt() * -1.0).toFloat()
                )
            )
        }catch (err:Exception){
            Log.e("dice","Error finding opponent roll view")
        }
    }

    fun displayRestart(yourScore: Int, winnerScore: Int, isWin: Boolean) {
        postgame_buttons.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        this.motionSensorController =
            GameApp.applicationContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        motionSensorController.getDefaultSensor(Sensor.TYPE_GYROSCOPE)?.let {
            this.gyroscope = it
        }
        motionSensorController.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
            this.accelerometer = it
        }
        /*motionSensorController.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
            this.accelerometer = it
        }*/
        motionSensorController.registerListener(gameObj, this.accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        motionSensorController.registerListener(gameObj, this.gyroscope, SensorManager.SENSOR_DELAY_NORMAL)

    }

    override fun onPause() {
        super.onPause()
        gameObj!!.onPause()
        this.motionSensorController.unregisterListener(gameObj)
    }

    companion object : GameFragment {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment FlipFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        override fun newInstance(game: Game): DiceFragment =
            DiceFragment().apply {
                gameObj = game
            }
    }
}
