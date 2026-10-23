package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;

public interface DataAccess {
    AuthData registerUser(UserData user) throws DataAccessException;
    AuthData loginUser(UserData user) throws DataAccessException;
    AuthData getAuthByToken(String authToken) throws DataAccessException;
    boolean logoutUser(String authToken) throws DataAccessException;
    UserData getUser(String username);
    int addGame(String gameName, ChessGame game);
    GameData getGame(int gameID);
    Collection<GameData> listGames();
    void clear();
}
