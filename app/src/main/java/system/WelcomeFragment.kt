package system

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import edu.us.ischool.bchong.info448project.R

class WelcomeFragment : Fragment() {

    private lateinit var startGameButton: Button
    private lateinit var enterName: EditText
    private lateinit var userName: String

    companion object {
        fun newInstance(): WelcomeFragment {
            val fragment = WelcomeFragment()
            val bundle = Bundle()
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
        val view = inflater.inflate(R.layout.fragment_welcome, container, false)
        handleView(view)
        return view
    }

    fun handleView(view: View) {
        startGameButton = view.findViewById(R.id.btnStartGame)
        startGameButton.isEnabled = false

        enterName = view.findViewById(R.id.editTextName)
        enterName.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                userName = enterName.text.toString()
                startGameButton.isEnabled = s.isNotBlank()
            }
        })

        startGameButton.setOnClickListener {
            val playModeFragment = PlayModeFragment.newInstance(userName)
            val transaction = fragmentManager!!.beginTransaction()
                transaction.setCustomAnimations(R.anim.pop_in, R.anim.pop_out)
                transaction.replace(R.id.fragmentmain, playModeFragment)
                transaction.commit()
        }
    }

}
