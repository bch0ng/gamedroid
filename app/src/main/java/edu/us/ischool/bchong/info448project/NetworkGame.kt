package edu.us.ischool.bchong.info448project

import android.os.Bundle
import game.Game

interface NetworkGame: Game,NetworkListener {
    fun onDisconnect()                      //Not yet implemented
    fun setId(id:String)                    //Should set this game's id.
    fun setNetworkPlayers(thisPlayers:ArrayList<Pair<String,String>>)
}