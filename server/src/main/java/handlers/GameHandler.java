package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import requests.CreateGameRequest;
import results.CreateGameResult;
import service.GameService;

public class GameHandler {
    Gson serializer = new Gson();
    GameService service;

    public GameHandler(GameService gameService) {
        this.service = gameService;
    }

    public void createGame(Context context) throws DataAccessException {
        String authToken = context.header("Authorization");
        CreateGameRequest request = new CreateGameRequest(authToken);
        CreateGameResult result = service.newGame(request);
        context.contentType("application/json");
        context.status(200).result(serializer.toJson(result));
    }
}
