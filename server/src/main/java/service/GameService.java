package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.ListGamesRequest;
import results.CreateGameResult;
import results.JoinGameResult;
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

    public synchronized JoinGameResult joinGame(JoinGameRequest request) throws DataAccessException {
        if (request == null) {
            throw new ServiceException(401, "Session provided is null");
        }
        AuthData authData = dataAccess.getAuthByToken(request.getAuthToken());
        if (authData == null) {
            throw new ServiceException(401, "Session does not exist");
        }
        if (request.getGameID() == null || request.getPlayerColor() == null) {
            throw new ServiceException(400, "Game ID and player color are required");
        }
        GameData game = dataAccess.getGame(request.getGameID());
        if (game == null) {
            throw new ServiceException(400, "Game does not exist");
        }
        boolean joiningWhite = request.getPlayerColor() == ChessGame.TeamColor.WHITE;
        String currentPlayer = joiningWhite ? game.getWhiteUsername() : game.getBlackUsername();
        if (currentPlayer != null) {
            throw new ServiceException(403, "Player color already taken");
        }
        dataAccess.updateGame(new GameData(game.getGameID(), game.getGameName(),
                joiningWhite ? authData.getUsername() : game.getWhiteUsername(),
                joiningWhite ? game.getBlackUsername() : authData.getUsername(), game.getGame()));
        return new JoinGameResult();
    }
}
