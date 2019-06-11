package edu.us.ischool.bchong.info448project

import game.Game
import game.GameFragment
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class TelephoneFragment : Fragment(), GameFragment {
    override fun setNetworkPlayers(thisPlayers: ArrayList<Pair<String, String>>) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setNetworkListener(networkListener: NetworkListener) {
       // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

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