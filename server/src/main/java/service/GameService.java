package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import requests.CreateGameRequest;
import results.CreateGameResult;

public class GameService {
    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public CreateGameResult newGame(CreateGameRequest request) throws DataAccessException {
        String authToken = request.getAuthToken();
        AuthData authData = dataAccess.getAuthByToken(authToken);
        if (authData == null) {
            throw new ServiceException(401, "Session does not exist");
        }
        ChessGame game = new ChessGame();
        int gameID = dataAccess.addGame(game);
        return new CreateGameResult(gameID);
    }
}
