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


class FragmentHostCode : Fragment() {
    companion object {
        private lateinit var randomCode: String
        fun newInstance(randomCode: String): FragmentHostCode {
            val fragment = FragmentHostCode()
            val bundle = Bundle()
            bundle.putString("Code", randomCode)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_hostcode, container, false)
        handleView(view)
        return view
    }

    fun handleView(view: View) {
        val btnEnter = view.findViewById<Button>(R.id.btnEnterRoom)
        val txtCode = view.findViewById<Button>(R.id.txtCode)
        txtCode.text = randomCode
        btnEnter.setOnClickListener {
            val intent = Intent(activity, GameroomActivity::class.java)
            intent.putExtra("Code", randomCode)
            startActivity(intent)
        }
    }
}
