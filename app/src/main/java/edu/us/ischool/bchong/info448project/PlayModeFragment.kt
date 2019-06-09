package edu.us.ischool.bchong.info448project

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button


class PlayModeFragment : Fragment() {

    private lateinit var playmode: String
    //private lateinit var username: String
    private lateinit var singlebtn: Button
    private lateinit var multibtn: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_play_mode, container, false)
        handleView(view)
        return view
    }

    fun handleView (view: View) {
        //username = arguments!!.getString("USERNAME")
        singlebtn = view.findViewById(R.id.btnSingle)
        multibtn = view.findViewById(R.id.btnMulti)

        singlebtn.setOnClickListener {
            multibtn.isEnabled = false
            playmode = "Single"
            val gameSelectionFragment = GamelistFragment.newInstance(playmode, "Host")
            val transaction = fragmentManager!!.beginTransaction()
            transaction.replace(R.id.fragments, gameSelectionFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        /*multibtn.setOnClickListener {
            singlebtn.isEnabled = false
            playmode = "Multi"
            val gameSelectionFragment = GameSelectionFragment.newInstance(playmode,null)
            val transaction = fragmentManager!!.beginTransaction()
            transaction.replace(R.id.fragments, gameSelectionFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }*/
    }
    companion object {
        @JvmStatic
        fun newInstance() =
            GamelistFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
