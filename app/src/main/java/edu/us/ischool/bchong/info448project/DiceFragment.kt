package edu.us.ischool.bchong.info448project

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.content.res.AppCompatResources
import android.text.Layout
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.TextView
import android.widget.Toast
import game.Game
import game.GameApp
import game.GameFragment
import kotlinx.android.synthetic.main.activity_main.view.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.roundToInt


class DiceFragment : Fragment(), GameFragment {
    override fun setNetworkListener(networkListener: NetworkListener) {
        this.localHost=networkListener as RollTheDiceHost
    }

    override fun newInstance(game: Game): GameFragment {
        gameObj = game as NetworkGame
        return this
    }


    private lateinit var nearby: NearbyConnection
    private var listener: OnFragmentInteractionListener? = null
    lateinit var gameObj: NetworkGame
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
    lateinit var players: ArrayList<Pair<String, String>>
    private lateinit var playerDiceVisual: ImageView

    //Animation variables
    var baseHeight = 100
    var baseWidth = 100
    lateinit var dimensionAnimators: HashMap<String, ValueAnimator>
    lateinit var playerDiceDimAnimator: ValueAnimator
    private var localHost:RollTheDiceHost?=null

    override fun setNetworkPlayers(thisPlayers:ArrayList<Pair<String,String>>){
        this.players=thisPlayers
        this.gameObj.setNetworkPlayers(thisPlayers)
    }

    //Shows the name of the player who won
    fun showWinner(winner: Pair<String, Int>) {
        val scoreString = "${winner.first} won with ${winner.second}"

        Log.v("dice", scoreString)
    }

    //When the starting message is received from the server
    fun StartGame(myId: Pair<String, String>, allPlayers: ArrayList<Pair<String, String>>) {
        randomCharSet=arrayOf<String>("$","?","@","ß","∫")
        Log.v("dice", "Start game called by server")
        players = allPlayers
        dimensionAnimators = hashMapOf<String, ValueAnimator>()
        player = myId
        val fragMananager = fragmentManager
        val fragTransaction = fragMananager!!.beginTransaction()

        redrawPlayers()
        fragTransaction.commit()
    }

    //Returns the id string used in view id's
    private fun getIdString(id: String): String {
        return "$id$ID_SUFFIX"
    }

    //Redraws all the players in their list in scrollview
    private fun redrawPlayers() {
        players.map { thisPlayer: Pair<String, String> ->
            val newDiceObj = LayoutInflater.from(context).inflate(R.layout.dice_opponent, null)
            newDiceObj.tag = getIdString(thisPlayer.first)
            newDiceObj.findViewWithTag<TextView>("player_name").setText(thisPlayer.second)
        }
    }

    //Draws all the players in the scrollview
    private fun drawPlayers(savedInstanceState: Bundle) {
        val playerNameTag: TextView = player_dice.findViewWithTag("player_name") as TextView
        //val inflater=context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (savedInstanceState.containsKey(PLAYERS_KEY)) {
            players = savedInstanceState.get(PLAYERS_KEY) as ArrayList<Pair<String, String>>
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

    //Saves the current players
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (players != null) {
            outState.putSerializable("players", players)
        }
        if (player != null) {
            outState.putSerializable("player", player)
        }
    }

    //When the game is started it will call this function.
    override fun onStart() {
        super.onStart()
        if(this.localHost!=null){
            localHost!!.setNearby(nearby)
            localHost!!.onStart()
        }
        this.gameObj!!.onFragmentStart()
    }

    //When the view is created
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

        nearby = NearbyConnection.instance
        if (nearby.isHosting()) {
        } else {
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        playerDiceDimAnimator = ValueAnimator()
    }
    fun sendMessage(bundle: Bundle){
        val keyset=bundle.keySet()
        var jsonObj = JSONObject()
        keyset.map {
            try {
                // json.put(key, bundle.get(key)); see edit below
                jsonObj.put(it, JSONObject.wrap(bundle.get(it)));
            } catch (ex:java.lang.Exception) {
                //Handle exception here
            }
        }
      nearby.sendMessageAll("dice:${jsonObj.toString()}")
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
    //Do not add a onAttach function
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
    }

    //Reveals the roll for the opponent dice with the given id
    fun revealRoll(id: String, rollValue: Int) {
        val parentElement = root_dice_layout.findViewWithTag<TextView>("${id}Dice")
        parentElement.findViewWithTag<TextView>("dice_text").text = "$rollValue"
    }



    //Turns off all timers animating the player and opponents dice
    private fun cancelAllVisualTimers() {
        dimensionAnimators.map {
            it.value.cancel()
        }
        playerDiceDimAnimator.cancel()
    }

    //Converts px values to dp units
    private fun dpToPx(dp: Int): Int {
        val density = context!!.resources.displayMetrics.density
        return Math.round(dp.toFloat() * density)
    }

    //Rotates and changes the size of a view
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
            //params.height = dpToPx((baseHeight * cosinedDimension).toInt())
            params.width = dpToPx((baseWidth * cosinedDimension).toInt())
            targetView.layoutParams = params
        }
        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator) {
                //animation.cancel()
                targetView.rotation=0f

            }

            override fun onAnimationCancel(animation: Animator) {
                targetView.layoutParams.height=baseHeight
                targetView.layoutParams.width=baseWidth
                targetView.rotation=0f
            }

            override fun onAnimationRepeat(animation: Animator) {}
        })
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.duration = milliDuration
        valueAnimator.start()
        return valueAnimator
    }

    //Displays the restart buttons, needs testing
    fun displayRestart(yourScore: Int, winnerScore: Int, isWin: Boolean) {
        postgame_buttons.visibility = View.VISIBLE
    }

    //Reregisters the motion sensors
    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(nearby.getContext()).registerReceiver(broadCastReceiver,
            IntentFilter("edu.us.ischool.bchong.info448project.ACTION_SEND")
        )
        this.motionSensorController =
            context!!.getSystemService(Context.SENSOR_SERVICE) as SensorManager
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
        LocalBroadcastManager.getInstance(nearby.getContext()).unregisterReceiver(broadCastReceiver)
        cancelAllVisualTimers()
        gameObj!!.onPause()
        this.motionSensorController.unregisterListener(gameObj)
    }

    val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            gameObj.newMessage(intent!!.extras)
            if(localHost!=null){
                localHost!!.sendMessage(intent!!.extras)
            }
        }
    }
    companion object : GameFragment {
        override fun setNetworkPlayers(thisPlayers: ArrayList<Pair<String, String>>) {

        }

        override fun setNetworkListener(networkListener: NetworkListener) {
            //Doesn't do anything ATM
        }

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
                gameObj = game as NetworkGame
            }
    }
}
