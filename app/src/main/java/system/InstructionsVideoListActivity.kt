package system

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import edu.us.ischool.bchong.info448project.R
import kotlinx.android.synthetic.main.instruction_video_list_element.*
import android.content.Intent
import android.content.ActivityNotFoundException
import android.net.Uri


class InstructionsVideoListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.instruction_video_list_element)
        instructions_pick_up_phone.setOnClickListener {
            startYoutubeVideo("MDt6O_bl3N8")
        }
        instructions_roll_dice.setOnClickListener({
            startYoutubeVideo("M75MbF6Zu-0")
        })
        instructions_flip_phone.setOnClickListener {
            startYoutubeVideo("xFNAjiHKXx8")
        }
        instructions_shake_soda.setOnClickListener({
            startYoutubeVideo("VDJuOcEAPq0")
        })

    }
    private fun startYoutubeVideo(vidId:String){
        val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$vidId"))
        val webIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("http://www.youtube.com/watch?v=$vidId")
        )
        try {
            startActivity(appIntent)
        } catch (ex: ActivityNotFoundException) {
            startActivity(webIntent)
        }
    }
}




