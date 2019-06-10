package System

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import edu.us.ischool.bchong.info448project.*

class GameroomActivity : AppCompatActivity(),
                         FindCreateRoomFragment.OnFragmentInteractionListener,
                         RoomLobbyFragment.OnFragmentInteractionListener
{
    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gameroom)

        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()

        val findCreateRoomFragment = FindCreateRoomFragment.newInstance()
        transaction.replace(R.id.fragment_find_create_room, findCreateRoomFragment)
        transaction.addToBackStack(null)
        transaction.commit()

    }
}