package game

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.Scroller
import edu.us.ischool.bchong.info448project.R
import system.InstructionsVideoListActivity
import system.MainActivity


class SodaShakeFragment : Fragment(), GameFragment {
    var gameObj: Game? = null
    var sodaImage: ImageView? = null
    var endBtns: ScrollView? = null
    var activityCommander: Context? = null

    override fun newInstance(game: Game): GameFragment {
        gameObj = game
        return this
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_soda_shake, container, false)
        endBtns = view.findViewById(R.id.end_btns)
        endBtns?.setVisibility(View.GONE)
        sodaImage = view.findViewById(R.id.soda_image)
        val backToMenuBtn = view.findViewById<Button>(R.id.back_to_menu_btn)
        backToMenuBtn.setOnClickListener() {
            (activityCommander as GameStateController).goBackToMenu()
        }
        val playAgainBtn = view.findViewById<Button>(R.id.play_again_btn)
        playAgainBtn.setOnClickListener() {
            (activityCommander as GameStateController).playAgain("Shake the Soda")

        }
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            activityCommander = context
        } catch(e: ClassCastException) {
            throw ClassCastException(context.toString())
        }
    }

    fun endGame() {
        endBtns?.setVisibility(View.VISIBLE)
        sodaImage?.setImageResource(R.drawable.sodabottleexplode)
    }

}
