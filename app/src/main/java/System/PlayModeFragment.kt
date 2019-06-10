package System

import Game.GameActivity
import Game.GamelistFragment
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import edu.us.ischool.bchong.info448project.R


class PlayModeFragment : Fragment() {

    private lateinit var playmode: String
    //private lateinit var username: String
    private lateinit var singlebtn: Button
    private lateinit var multibtn: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
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
            val intent = Intent(activity, GameActivity::class.java)
            intent.putExtra("IDENTITY", "Host")
            intent.putExtra("GAMEMODE","Single")
            startActivity(intent)
            getActivity()!!.finish()
        }

        multibtn.setOnClickListener {
            //TODO
        }
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