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


class FragmentGuestCode : Fragment() {
    companion object {
        //        private lateinit var
        fun newInstance(): FragmentGuestCode {
            val fragment = FragmentGuestCode()
            val bundle = Bundle()
//            bundle.putString()
            fragment.arguments = bundle
            return fragment
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_guestcode, container, false)
        handleView(view)
        return view
    }

    fun handleView(view: View) {
        val txtEnterCode = view.findViewById<Button>(R.id.txtEnterCode)
        val code = txtEnterCode.text.toString()
        val btnEnter = view.findViewById<Button>(R.id.btnEnterRoom)
        btnEnter.setOnClickListener {
            val intent = Intent(activity, GameroomActivity::class.java)
            intent.putExtra("Code", code)
            startActivity(intent)
        }
    }
}

