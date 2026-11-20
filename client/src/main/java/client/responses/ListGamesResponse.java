package client.responses;

import model.GameSummary;

import java.util.List;

public class ListGamesResponse {
    final List<GameSummary> games;

    public ListGamesResponse(List<GameSummary> games) {
        this.games = games;
    }

    public List<GameSummary> getGames() {
        return games;
    }

}
