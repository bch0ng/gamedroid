package System

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import edu.us.ischool.bchong.info448project.*

class GameroomActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
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