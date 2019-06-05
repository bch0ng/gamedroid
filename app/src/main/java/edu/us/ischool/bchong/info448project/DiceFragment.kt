package edu.us.ischool.bchong.info448project

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
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
import android.view.animation.LinearInterpolator
import android.widget.TextView
import java.util.*
import kotlin.collections.HashMap
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

    private lateinit var randomCharSet:Array<String>

    val PLAYER_KEY = "player"
    val PLAYERS_KEY = "players"
    val ID_SUFFIX = "Dice"
    lateinit var player: Pair<String, String>
    lateinit var players: Array<Pair<String, String>>
    private lateinit var playerDiceVisual: ImageView

    //Animation variables
    var baseHeight = 100
    var baseWidth = 100
    lateinit var dimensionAnimators: HashMap<String, ValueAnimator>
    lateinit var playerDiceDimAnimator: ValueAnimator

    fun ShowWinner(winner: Pair<String, Int>) {
        val scoreString = "${winner.first} won with ${winner.second}"
        Log.v("dice", scoreString)
    }

    //Should be called by the client when the starting message is recieved from the server
    fun StartGame(myId: Pair<String, String>, allPlayers: Array<Pair<String, String>>) {
        randomCharSet=arrayOf<String>("$","?","@","ß","∫")
        Log.v("dice", "Start game called by server")
        var index = 0
        players = allPlayers
        /*allPlayers.map {
            players[index]=it
            index++
        }*/
        playerDiceDimAnimator = ValueAnimator()
        dimensionAnimators = hashMapOf<String, ValueAnimator>()
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

    //Starts the player rolling animation with the given force
    //Eat unit of force equals 1 radian of rotation per second
    fun diceRoll(force1: Float, force2: Float, currentRollEnergy: Double, duration: Long) {
        // TODO: test visuals
        if (playerDiceDimAnimator.isRunning) {
            //playerDiceDimAnimator.end()
        }else{
            playerDiceDimAnimator = setDimAnimator(player_dice, duration)
        }
        //val dimensionAnimator = ValueAnimator.ofFloat(0f, (Math.PI * duration).toFloat())
        //dimensionAnimator.duration = duration
        //dimensionAnimator.addUpdateListener(dimAnimator(player_dice, force1, force2))
    }

    /*
    //Changes the size of dice
    private fun dimAnimator(element: View, force1: Float, force2: Float) =
        object : ValueAnimator.AnimatorUpdateListener {
            override fun onAnimationUpdate(animation: ValueAnimator) {
                Log.v("dice", "Animation Update")
                val animatedValue: Float = animation.animatedValue as Float
                element.layoutParams.height = baseHeight * (Math.cos(force1 * animatedValue.toDouble())).toInt()
                element.layoutParams.width = baseWidth * (Math.cos(force2 * animatedValue.toDouble())).toInt()
            }
        }*/


    fun revealRoll(id: String, rollValue: Int) {
        val parentElement = root_dice_layout.findViewWithTag<TextView>("${id}Dice")
        parentElement.findViewWithTag<TextView>("dice_text").text = "$rollValue"
    }

    //Do the visuals for when a opponents dice is rolled
    fun opponentRolled(id: String, strength: Double, duration: Long) {
        // TODO:
        try {
            Log.v("diceSet", "Opponent Rolled Fragment listener started")
            val elementGroup: ViewGroup = opponent_dice.findViewWithTag(getIdString(id)) as ViewGroup
            val element: View = elementGroup.findViewWithTag("dice_img")
            /*val dimensionAnimator = ValueAnimator.ofFloat(0f, (Math.PI * duration).toFloat())
            dimensionAnimator.duration = duration
            val animator = /*dimAnimator(
                element,
                (strength * Math.random().roundToInt() * -1.0).toFloat(),
                (strength * Math.random().roundToInt() * -1.0).toFloat()
            )*/
            dimensionAnimator.addUpdateListener(
                animator
            )*/
            if (dimensionAnimators.containsKey(id)) {
                val dimensionAnimator = dimensionAnimators.get(id)
            } else {
                val element: View = opponent_dice.findViewWithTag(getIdString(id))
                dimensionAnimators.put(id, setDimAnimator(element, duration))
            }
        } catch (err: Exception) {
            Log.e("dice", "Error finding opponent roll view")
        }
    }

    //Turns off all timers animating the player and opponents dice
    private fun cancelAllVisualTimers() {
        dimensionAnimators.map {
            it.value.cancel()
        }
        playerDiceDimAnimator.cancel()
    }

    fun dpToPx(dp: Int): Int {
        val density = context!!.resources.displayMetrics.density
        return Math.round(dp.toFloat() * density)
    }

    fun setDimAnimator(targetView: View, milliDuration: Long): ValueAnimator {
        Log.v("diceSet", "New animator set for $milliDuration")
        var targetTextView=(targetView as ViewGroup).findViewWithTag<TextView>("dice_text")
        var valueAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, Math.PI.toFloat() * 4f)
        valueAnimator.addUpdateListener {
            val animatedVal = it.animatedValue as Float
            var cosinedDimension = Math.cos(animatedVal.toDouble())
            targetView.rotation=(cosinedDimension*Math.PI*180f).toFloat()
            targetTextView.text=randomCharSet.random()

            Log.v("dice", "value changed to $animatedVal :$cosinedDimension");
            var params = targetView.layoutParams
            params.height = dpToPx((baseHeight * cosinedDimension).toInt())
            params.width = dpToPx((baseWidth * cosinedDimension).toInt())
            targetView.layoutParams = params
        }
        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator) {
                //animation.cancel()
            }

            override fun onAnimationCancel(animation: Animator) {
                targetView.layoutParams.height=baseHeight
                targetView.layoutParams.width=baseWidth
                //targetView.rotation=0f
            }

            override fun onAnimationRepeat(animation: Animator) {}
        })
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.duration = milliDuration
        valueAnimator.start()
        return valueAnimator
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
        cancelAllVisualTimers()
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
