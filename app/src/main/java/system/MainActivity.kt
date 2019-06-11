package system

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.app.AlertDialog
import android.content.Intent
import edu.us.ischool.bchong.info448project.NearbyConnection
import edu.us.ischool.bchong.info448project.R
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(){
    //Permission check based on version
    private lateinit var startGameButton: Button
    private lateinit var enterName: EditText
    private lateinit var userName: String

    private fun checkForMotionSensors(){
        val packageManager = packageManager
        val hasGyro=packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE)
        val hasAcc=packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER)
        if(!hasGyro||!hasAcc){
            val dialog = AlertDialog.Builder(this)
                .setTitle(R.string.no_sensor_title)
                .setMessage(R.string.no_sensor_text)
                .setPositiveButton(R.string.no_sensor_affirmative,{
                    dialogInterface, i ->

                })
                .create()
            dialog.show()
        }
    }
    override fun onStart()
    {
        super.onStart()
        checkForMotionSensors()
        if (Build.VERSION.SDK_INT < 28) {
            if (ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    8034
                )
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    8035
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        NearbyConnection.initialize(this)

        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()

        val welcomePage = WelcomeFragment.newInstance()
        transaction.replace(R.id.fragmentmain, welcomePage)
        transaction.commit()

        instructions_videos_page_button.setOnClickListener {
            val intent = Intent(this, system.InstructionsVideoListActivity::class.java)
            startActivity(intent)
        }
    }
}




