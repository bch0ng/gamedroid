package edu.us.ischool.bchong.info448project

import android.support.v4.app.Fragment
import java.io.Serializable

interface Game :Serializable{
    var gameFragment: Fragment
    fun OnStart()
    fun OnEnd():Int

}