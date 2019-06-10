package System

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import edu.us.ischool.bchong.info448project.R

class GameroomActivity : AppCompatActivity(){
    private lateinit var identity: String
    private lateinit var HostButton : Button
    private lateinit var HostGuest1 : Button
    private lateinit var HostGuest2 : Button
    private lateinit var HostGuest3 : Button
    private lateinit var HostGuest4 : Button
    private lateinit var HostGuest5 : Button
    private lateinit var username: String
    private lateinit var playmode: String


    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_gameroom)
        identity = this.intent.getStringExtra("IDENTITY")
        HostButton = findViewById(R.id.buttonHost)
        HostGuest1 = findViewById(R.id.buttonGuest1)
        HostGuest2 = findViewById(R.id.buttonGuest2)
        HostGuest3 = findViewById(R.id.buttonGuest3)
        HostGuest4 = findViewById(R.id.buttonGuest4)
        HostGuest5 = findViewById(R.id.buttonGuest5)
        var hostArray = arrayOf(HostButton, HostGuest1, HostGuest2, HostGuest3, HostGuest4, HostGuest5)


    }

}