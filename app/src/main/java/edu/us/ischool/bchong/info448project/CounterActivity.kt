package edu.us.ischool.bchong.info448project

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class CounterActivity : AppCompatActivity() {

    private var scoreMap = hashMapOf<String, Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_counter)
    }

    // How is the host gonna store the data stream from the clients
}