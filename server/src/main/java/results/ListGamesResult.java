package results;

import model.GameData;
import model.GameSummary;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public class ListGamesResult {
    final List<GameSummary> games;

    public ListGamesResult(Collection<GameData> games) {
        List<GameSummary> summaries = new ArrayList<>();
        for (GameData game : games){
            GameSummary summary = new GameSummary(game.getGameID(), game.getGameName(),
                    game.getWhiteUsername(), game.getBlackUsername());
            summaries.add(summary);
        }
        this.games = List.copyOf(summaries);
    }

    public List<GameSummary> getGames() {
        return games;
    }

}
