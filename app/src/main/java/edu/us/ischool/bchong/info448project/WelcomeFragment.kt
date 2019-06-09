package edu.us.ischool.bchong.info448project

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.w3c.dom.Text

class WelcomeFragment : Fragment() {

    private var listener: OnPlaymodetInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view =  inflater.inflate(R.layout.fragment_welcome, container, false)
        //Send username to host
        var nameEdit = view.findViewById<EditText>(R.id.editTextName)
        var welcomebutton = view.findViewById<Button>(R.id.btnWelcome)
        welcomebutton.setOnClickListener{
            listener?.onPlaymodeInteraction()
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnPlaymodetInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
    interface OnPlaymodetInteractionListener {
        fun onPlaymodeInteraction()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            WelcomeFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
