package Game

import android.hardware.SensorEventListener
import java.io.Serializable

interface Game: Serializable, SensorEventListener {
    var gameFragment: GameFragment?
    fun OnStart()
    fun OnRegisterMotionListener()
    fun OnEnd():Int

}