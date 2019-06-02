package edu.us.ischool.bchong.info448project

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button


class FragmentHostOrGuest : Fragment() {

    companion object {
//        private lateinit var
        fun newInstance(): FragmentHostOrGuest {
            val fragment = FragmentHostOrGuest()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_hostorguest, container, false)
        handleView(view)
        return view
    }


    fun handleView(view: View) {
        val btnHost = view.findViewById<Button>(R.id.btnHost)
        val btnGuest = view.findViewById<Button>(R.id.btnGuest)

        btnHost.setOnClickListener {
            // to be implemented
            val randomCode: String = "0124"
            val fragmentHostCode = FragmentHostCode.newInstance(randomCode)
            val transaction = fragmentManager!!.beginTransaction()
            transaction.replace(R.id.fragments, fragmentHostCode)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        btnGuest.setOnClickListener {
            val fragmentGuestCode = FragmentGuestCode.newInstance()
            val transaction = fragmentManager!!.beginTransaction()
            transaction.replace(R.id.fragments, fragmentGuestCode)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
}
