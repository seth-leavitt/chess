package results;

import model.GameData;

import java.util.Collection;
import java.util.List;

public class ListGamesResult {
    final List<GameSummary> games;

    public ListGamesResult(Collection<GameData> games) {
        this.games = games.stream().map(GameSummary::new).toList();
    }

    public List<GameSummary> getGames() {
        return games;
    }

    public static class GameSummary {
        final int gameID;
        final String gameName;
        final String whiteUsername;
        final String blackUsername;

        public GameSummary(GameData game) {
            this.gameID = game.getGameID();
            this.gameName = game.getGameName();
            this.whiteUsername = game.getWhiteUsername();
            this.blackUsername = game.getBlackUsername();
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
