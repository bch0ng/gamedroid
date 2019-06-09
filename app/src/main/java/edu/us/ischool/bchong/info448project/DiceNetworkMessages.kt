package edu.us.ischool.bchong.info448project
enum class DiceNetworkMessages(val code: String) {
    START_GAME("startGame"),                    //When a game is started, send the players and their id's
    NEW_TURN("newTurn"),                         //When someone's turn is starting, sent to the clients with their id
    OPPONENT_SHAKE("shake"),                        //When a shake event happens, id and strength
    OPPONENT_DISCONNECT("opponentDisconnect"),      //When a player disconnects
    OPPONENT_SCORE("opponentScore"),                //Sends the score and id of a player to another player
    GAME_OVER("gameOver")                           //When the game is over send a Array<Pair<String, Int>> of id's and scores
}