package system

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import edu.us.ischool.bchong.info448project.*
import android.content.Intent



class GameroomActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gameroom)

        val multiRoomService = Intent(this, MultiRoomService::class.java)
        startService(multiRoomService)

        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()

        val findCreateRoomFragment = FindCreateRoomFragment.newInstance()
        transaction.replace(R.id.fragment_find_create_room, findCreateRoomFragment)
        transaction.commit()

    }
}