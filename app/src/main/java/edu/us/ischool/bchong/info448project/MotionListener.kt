package edu.us.ischool.bchong.info448project

import android.hardware.SensorEvent

interface MotionListener {
    fun everyFrame(event: SensorEvent?)
    fun OnFlip()
}