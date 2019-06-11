package game

interface GameStateController {
    fun goBackToMenu()
    fun playAgain(gameName: String)
}