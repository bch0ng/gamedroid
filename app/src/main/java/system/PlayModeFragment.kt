package system

import game.GameActivity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import edu.us.ischool.bchong.info448project.NearbyConnection
import edu.us.ischool.bchong.info448project.R


class PlayModeFragment : Fragment() {

    private lateinit var playmode: String
    private lateinit var username: String
    private lateinit var singlebtn: Button
    private lateinit var multibtn: Button
    private lateinit var playmodetextview:TextView

    companion object {
        fun newInstance(username: String): PlayModeFragment {
            val fragment = PlayModeFragment()
            val bundle = Bundle()
            bundle.putString("USERNAME", username)
            fragment.arguments = bundle
            return fragment
        }
    }

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
        username = arguments!!.getString("USERNAME")
        singlebtn = view.findViewById(R.id.btnSingle)
        multibtn = view.findViewById(R.id.btnMulti)
        playmodetextview = view.findViewById(R.id.textViewplaymode1)
        playmodetextview.setText("Hi, $username!")

        singlebtn.setOnClickListener {
            playmode = "Single"
            val intent = Intent(activity, GameActivity::class.java)
                intent.putExtra("USERNAME",username)
                intent.putExtra("IDENTITY", "Host")
                intent.putExtra("GAMEMODE","Single")
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
        }

        multibtn.setOnClickListener {
            NearbyConnection.instance.setUsername(username)
            val intent = Intent(activity, GameroomActivity::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
        }
    }

}
