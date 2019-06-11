package edu.us.ischool.bchong.info448project

import android.os.Bundle

interface NetworkListener {
    fun newMessage(message: Bundle)                              //Get's a message from a client
    fun sendMessage(message: Bundle)
}