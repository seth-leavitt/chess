package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.UserData;

public interface DataAccess {
    AuthData registerUser(UserData user) throws DataAccessException;
    AuthData loginUser(UserData user) throws DataAccessException;
    AuthData getAuthByToken(String authToken);
    boolean logoutUser(String authToken) throws DataAccessException;
    UserData getUser(String username);
    int addGame(ChessGame game);
    ChessGame getGame(int gameID);
    void clear();
}
