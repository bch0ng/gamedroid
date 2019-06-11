package game

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import edu.us.ischool.bchong.info448project.R

class SodaShakeFragment : Fragment(), GameFragment {
    var gameObj: Game? = null
    var sodaImage: ImageView? = null

    override fun newInstance(game: Game): GameFragment {
        gameObj = game
        return this
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_soda_shake, container, false)
        sodaImage = view.findViewById(R.id.soda_image)
        return view
    }

    fun endGame() {
        sodaImage?.setImageResource(R.drawable.sodabottleexplode)
    }

}
