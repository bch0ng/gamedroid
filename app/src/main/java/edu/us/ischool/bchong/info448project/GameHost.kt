package edu.us.ischool.bchong.info448project

import android.os.Bundle
import java.io.Serializable

interface GameHost: Serializable,NetworkListener {
    var localClient:NetworkGame
    fun newInstance(game:NetworkGame):GameHost                  //Returns an instance of itself with game set
                       //Sends a message to the client, get the client's id from the bundle
    fun setNetworkPlayers(players:ArrayList<String>)      //Gets the players id's and names from the network interface
    fun kickPlayer(id:String)                                   //Not yet implemented
    fun onStart()                                               //Should be called by the application
    fun onEnd()                                                 //Should be called by the application
}