package results;

import model.GameData;
import model.GameSummary;

import java.util.Collection;
import java.util.List;

public class ListGamesResult {
    final List<GameSummary> games;

    public ListGamesResult(Collection<GameData> games) {
        this.games = games.stream().map(game -> new GameSummary(game.getGameID(), game.getGameName(),
                game.getWhiteUsername(), game.getBlackUsername())).toList();
    }

    public List<GameSummary> getGames() {
        return games;
    }

}
