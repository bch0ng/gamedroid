package edu.us.ischool.bchong.info448project

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class TelephoneFragment : Fragment(), GameFragment {
    var gameObj: Game? = null
    var gameMessage: TextView? = null

    override fun newInstance(game: Game): GameFragment {
        gameObj = game
        return this
    }

    fun showWinText() {
        gameMessage?.setText("You win!")
    }

    fun showLoseText() {
        gameMessage?.setText("You lose!")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_telephone, container, false)

        gameMessage = view.findViewById(R.id.telephone_message)

        return view
    }
}
