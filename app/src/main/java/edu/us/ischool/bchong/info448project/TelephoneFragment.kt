package edu.us.ischool.bchong.info448project

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView


class TelephoneFragment : Fragment(), GameFragment {
    var gameObj: Game? = null

    override fun newInstance(game: Game): GameFragment {
        gameObj = game
        return this
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_telephone, container, false)
        return view
    }
}
