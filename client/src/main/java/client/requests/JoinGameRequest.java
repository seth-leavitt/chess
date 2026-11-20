package client.requests;

import chess.ChessGame;

public class JoinGameRequest {
    ChessGame.TeamColor playerColor;
    Integer gameID;

    public JoinGameRequest(ChessGame.TeamColor playerColor, Integer gameID) {
        this.playerColor = playerColor;
        this.gameID = gameID;
    }
}
