package game

import java.io.Serializable

interface GameFragment:Serializable {
    fun newInstance(game: Game): GameFragment
}