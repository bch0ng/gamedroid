package Game


import android.os.Bundle
import java.io.Serializable

interface GameHost: Serializable {
    var localClient:NetworkGame
    fun newInstance(game:NetworkGame):GameHost                  //Returns an instance of itself with game set
    fun newMessage(message:Bundle)                              //Get's a message from a client
    fun sendMessage(message: Bundle)                            //Sends a message to the client, get the client's id from the bundle
    fun setPlayers(players:ArrayList<Pair<String,String>>)      //Gets the players id's and names from the network interface
    fun kickPlayer(id:String)                                   //Not yet implemented
    fun onStart()                                               //Should be called by the application
    fun onEnd()                                                 //Should be called by the application
}
