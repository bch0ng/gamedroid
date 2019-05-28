package edu.us.ischool.bchong.info448project

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




/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [DiceFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [DiceFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class DiceFragment : Fragment(),GameFragment {


    lateinit var canvas:ImageView
    override fun newInstance(game: Game): GameFragment {
        gameObj=game
        return this
    }

    // TODO: Rename and change types of parameters
    private var listener: OnFragmentInteractionListener? = null
    var gameObj:Game?=null
    lateinit var gyroscope:Sensor
    lateinit var accelerometer:Sensor
    //lateinit var linearAccelerometer:SensorManager
    lateinit var motionSensorController: SensorManager
    //lateinit var accelerometer:Sensor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        restart_button.setOnClickListener {
            val ft = fragmentManager!!.beginTransaction()
            if (Build.VERSION.SDK_INT >= 26) {
                ft.setReorderingAllowed(false)
            }
            postgame_buttons.visibility=View.GONE
            ft.detach(this).attach(this).commit()
        }
        end_game_button.setOnClickListener {
            postgame_buttons.visibility=View.GONE
            activity!!.finish()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.flip, container, false)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
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
    //Do the visuals for when a dice is rolled
    fun diceRoll(force1:Float,force2:Float, currentRollEnergy:Double){
        // TODO:

    }
    //Do the visuals for when a opponents dice is rolled
    fun opponentRolled(strength:Double){
        // TODO:

    }
    fun displayRestart(yourScore:Int,winnerScore:Int, isWin:Boolean){
        postgame_buttons.visibility=View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        this.motionSensorController =GameApp.applicationContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        motionSensorController.getDefaultSensor(Sensor.TYPE_GYROSCOPE)?.let {
            this.gyroscope = it
        }
        motionSensorController.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
            this.accelerometer = it
        }
        /*motionSensorController.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
            this.accelerometer = it
        }*/
        motionSensorController.registerListener(gameObj,this.accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        motionSensorController.registerListener(gameObj,this.gyroscope, SensorManager.SENSOR_DELAY_NORMAL)

    }

    override fun onPause() {
        super.onPause()
        this.motionSensorController.unregisterListener(gameObj)
    }

    companion object :GameFragment{
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment FlipFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        override fun newInstance(game:Game):DiceFragment =
            DiceFragment().apply {
                gameObj=game
            }
    }
}
