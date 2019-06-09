package edu.us.ischool.bchong.info448project

import java.io.Serializable

interface GameFragment:Serializable {
    //Returns a instance of itself with the game set
    fun newInstance(game:Game):GameFragment

}