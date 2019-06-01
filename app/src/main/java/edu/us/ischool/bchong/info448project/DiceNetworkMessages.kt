package edu.us.ischool.bchong.info448project
enum class DiceNetworkMessages(val code: String) {
    START_GAME("startGame"),
    OPPONENT_SHAKE("shake"),
    OPPONENT_DISCONNECT("opponentDisconnect"),
    OPPONENT_SCORE("opponentScore"),
    GAME_OVER("gameOver")
}