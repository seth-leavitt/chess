package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import requests.CreateGameRequest;
import requests.ListGamesRequest;
import results.CreateGameResult;
import results.ListGamesResult;

public class GameService {
    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public CreateGameResult newGame(CreateGameRequest request) throws DataAccessException {
        if (request == null){
            throw new ServiceException(401, "Session provided is null");
        }
        String authToken = request.getAuthToken();
        AuthData authData = dataAccess.getAuthByToken(authToken);
        if (authData == null) {
            throw new ServiceException(401, "Session does not exist");
        }
        if (request.getGameName() == null) {
            throw new ServiceException(400, "Game name is required");
        }
        ChessGame game = new ChessGame();
        int gameID = dataAccess.addGame(request.getGameName(), game);
        return new CreateGameResult(gameID);
    }

    public ListGamesResult listGames(ListGamesRequest request) throws DataAccessException {
        if (request == null || dataAccess.getAuthByToken(request.getAuthToken()) == null) {
            throw new ServiceException(401, "Session does not exist");
        }
        return new ListGamesResult(dataAccess.listGames());
    }
}
