package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.ListGamesRequest;
import results.CreateGameResult;
import results.JoinGameResult;
import results.ListGamesResult;
import service.GameService;

public class GameHandler {
    Gson serializer = new Gson();
    GameService service;

    public GameHandler(GameService gameService) {
        this.service = gameService;
    }

    public void createGame(Context context) throws DataAccessException {
        String authToken = context.header("Authorization");
        CreateGameRequest body = serializer.fromJson(context.body(), CreateGameRequest.class);
        CreateGameRequest request = new CreateGameRequest(authToken, body == null ? null : body.getGameName());
        CreateGameResult result = service.newGame(request);
        context.contentType("application/json");
        context.status(200).result(serializer.toJson(result));
    }

    public void listGames(Context context) throws DataAccessException {
        String authToken = context.header("Authorization");
        ListGamesRequest request = new ListGamesRequest(authToken);
        ListGamesResult result = service.listGames(request);
        context.contentType("application/json");
        context.status(200).result(serializer.toJson(result));
    }

    public void joinGame(Context context) throws DataAccessException {
        String authToken = context.header("Authorization");
        JoinGameRequest body = serializer.fromJson(context.body(), JoinGameRequest.class);
        JoinGameRequest request = new JoinGameRequest(authToken,
                body == null ? null : body.getPlayerColor(), body == null ? null : body.getGameID());
        JoinGameResult result = service.joinGame(request);
        context.contentType("application/json");
        context.status(200).result(serializer.toJson(result));
    }
}
