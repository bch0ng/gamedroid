package edu.us.ischool.bchong.info448project

import android.hardware.SensorEventListener
import android.support.v4.app.Fragment
import java.io.Serializable

interface Game :Serializable,SensorEventListener{
    var gameFragment: GameFragment?
    fun OnStart()
    fun OnRegisterMotionListener()
    fun OnEnd():Int

}