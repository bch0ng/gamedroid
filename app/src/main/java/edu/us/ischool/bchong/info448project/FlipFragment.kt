package edu.us.ischool.bchong.info448project

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.flip.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FlipFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FlipFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FlipFragment : Fragment(),GameFragment {
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
    fun OnFlip(score:Int){
        flip_counter.text="score: $score"
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
        override fun newInstance(game:Game):FlipFragment =
            FlipFragment().apply {
                gameObj=game
            }
    }
}
