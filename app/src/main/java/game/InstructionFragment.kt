package game

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import edu.us.ischool.bchong.info448project.R
import org.json.JSONObject

private const val GAMECHOICE = "GAMECHOICE"

class InstructionFragment : Fragment() {
    private var gameChoice: String? = null
   private  lateinit var instructtext : TextView
    private var instructiondata :JSONObject= JSONObject("""{
        |"Shake the Soda" : "Shake the soda is a party game where players sit around in a circle and pass the phone around. When they get the phone, each player shakes it like a soda as many times as they want. If the soda explodes on you then you lose."
        |"Flip the Phone" : "Flip the phone is a single player game promoting physical activity and fun! Our scoring systems rewards players for any kind of tricks and moves they preform with their phone. Flip it in your hand or play catch with your friends. The possibilities are endless.

"
        |"Answer the Phone" : "Answer the phone is a multi-player game where each player places their phones face down in front of them. When the phones ring, the first player to flip their phone over wins."
        |"Roll the Dice" : "Roll the dice is a deception based party game for all your friends. Take turns rolling dice and making bets on who's the winner by feeling vibrations from your phone."
        |}""".trimMargin())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            gameChoice = it.getString(GAMECHOICE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_instruction, container, false)
        var instruct = instructiondata.getString(gameChoice)
        instructtext = view.findViewById(R.id.instructtextview)
        instructtext.setText(instruct)
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(gamechoice: String) =
            InstructionFragment().apply {
                arguments = Bundle().apply {
                    putString(GAMECHOICE, gamechoice)
                }
            }
    }
}
