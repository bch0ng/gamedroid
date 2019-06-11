package game

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.us.ischool.bchong.info448project.R
import kotlinx.android.synthetic.main.fragment_flip.*
import kotlinx.android.synthetic.main.fragment_score_board.*


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

    //Return an instance of this fragment with the game set
    override fun newInstance(game: Game): GameFragment {
        gameObj=game
        return this
    }
    override fun onStart(){
        super.onStart()
        this.gameObj!!.onFragmentStart()
    }

    private var listener: OnFragmentInteractionListener? = null
    lateinit var gameObj:Game
    lateinit var gyroscope:Sensor
    lateinit var accelerometer:Sensor
    //lateinit var linearAccelerometer:SensorManager
    lateinit var motionSensorController: SensorManager
    //lateinit var accelerometer:Sensor

    var colorGMin=12
    var colorBMin=50
    var colorG=12
    var colorB=50
    var colorGMax=102
    var colorBMax=132
    val name="?????"
    override fun onStop() {
        super.onStop()
        if(gameObj!=null){
            val gameActivity=activity as GameActivity
           // gameActivity.showScoreBoard(name,"Flip",gameObj.onEnd())
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }
    //fun onBackPressed() {
       // onStop()
    //}

    //Set the buttons
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        safety_compliance_button.setOnClickListener {
            safety_layout.visibility=View.GONE
        }
    }

    //The spinning squares color delta
    private fun incrementColors(increment:Int){
        colorG+=increment
        colorB+=increment
        if(colorB>colorBMax){
            colorB=colorBMin
        }
        if(colorG>colorGMax){
            colorG=colorGMin
        }
    }

    //Rotating the spinning squares formula
    fun rotateFlipAccent(power:Int){
        flip_score_background.rotation+=power
        flip_score_background2.rotation+=power*2
        incrementColors(power)
        flip_score_background2.setColorFilter(Color.rgb(255,colorG,colorB))
        flip_score_background.setColorFilter(Color.rgb(0,255-colorG,240-colorB))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_flip, container, false)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    /*override fun onAttach(context: Context) {
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

    //Setting the score visually.
    fun OnFlip(score:Int){
        flip_counter.text="$score"
    }

    //
    override fun onResume() {
        super.onResume()
        this.motionSensorController =context!!.getSystemService(Context.SENSOR_SERVICE) as SensorManager
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
        this.gameObj!!.onPause()
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