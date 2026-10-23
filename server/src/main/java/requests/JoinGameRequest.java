package requests;

import chess.ChessGame;

public class JoinGameRequest {
    String authToken;
    ChessGame.TeamColor playerColor;
    Integer gameID;

    public JoinGameRequest(String authToken, ChessGame.TeamColor playerColor, Integer gameID) {
        this.authToken = authToken;
        this.playerColor = playerColor;
        this.gameID = gameID;
    }

    public String getAuthToken() {
        return authToken;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }

    public Integer getGameID() {
        return gameID;
    }
}
