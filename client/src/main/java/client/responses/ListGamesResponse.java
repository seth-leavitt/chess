package client.responses;

import java.util.List;

public class ListGamesResponse {
    final List<GameSummary> games;

    public ListGamesResponse(List<GameSummary> games) {
        this.games = games;
    }

    public List<GameSummary> getGames() {
        return games;
    }

    public static class GameSummary {
        final int gameID;
        final String gameName;
        final String whiteUsername;
        final String blackUsername;

        public GameSummary(int gameID, String gameName, String whiteUsername, String blackUsername) {
            this.gameID = gameID;
            this.gameName = gameName;
            this.whiteUsername = whiteUsername;
            this.blackUsername = blackUsername;
        }

        public int getGameID() {
            return gameID;
        }

        public String getGameName() {
            return gameName;
        }

        public String getWhiteUsername() {
            return whiteUsername;
        }

        public String getBlackUsername() {
            return blackUsername;
        }
    }
}
