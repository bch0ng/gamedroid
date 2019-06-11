package game

import edu.us.ischool.bchong.info448project.NetworkListener
import java.io.Serializable

interface GameFragment:Serializable {
    fun newInstance(game: Game): GameFragment
    fun setNetworkListener(networkListener:NetworkListener)
    fun setNetworkPlayers(thisPlayers:ArrayList<Pair<String,String>>)
    }