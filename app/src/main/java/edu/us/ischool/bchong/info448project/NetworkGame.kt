package edu.us.ischool.bchong.info448project

import android.os.Bundle
import game.Game

interface NetworkGame: Game {
    fun newMessage(message:Bundle)          //Receive a message to the server
    fun sendMessage(message: Bundle)        //Send a message to the server
    fun onDisconnect()                      //Not yet implemented
    fun setId(id:String)                    //Should set this game's id.
}