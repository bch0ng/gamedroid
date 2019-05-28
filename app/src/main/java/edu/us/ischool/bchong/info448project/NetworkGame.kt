package edu.us.ischool.bchong.info448project

import android.os.Bundle

interface NetworkGame:Game {
    fun newMessage(message:Bundle)
    fun sendMessage(message: Bundle)
}