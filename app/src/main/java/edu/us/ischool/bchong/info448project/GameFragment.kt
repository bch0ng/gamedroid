package edu.us.ischool.bchong.info448project

import java.io.Serializable

interface GameFragment:Serializable {
    fun newInstance(game:Game):GameFragment
}