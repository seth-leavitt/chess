package dataaccess;

import java.util.HashMap;
import java.util.UUID;

import chess.ChessGame;
import model.AuthData;
import model.UserData;

public class MemoryDataAccess implements DataAccess {
    HashMap<String, UserData> users = new HashMap<>();
    HashMap<String, AuthData> authTokens = new HashMap<>();
    HashMap<UUID, ChessGame> games = new HashMap<>();

    public AuthData registerUser(UserData userData) throws DataAccessException {
        if (getUser(userData.getUsername()) != null){
            throw new DataAccessException("User Already Exists");
        }
        users.put(userData.getUsername(), userData);
        return loginUser(userData);
    }

    public AuthData loginUser(UserData userData) throws DataAccessException {
        if (getUser(userData.getUsername()) == null){
            throw new DataAccessException("User does not exists");
        }
        AuthData authData = new AuthData(UUID.randomUUID().toString(), userData.getUsername());
        authTokens.put(authData.getAuthToken(), authData);
        return authData;
    }

    public AuthData getAuth(String authToken) {
        return authTokens.get(authToken);
    }

    public boolean logoutUser(String authToken) throws DataAccessException {
        if (authTokens.containsKey(authToken)){
            authTokens.remove(authToken);
            return true;
        } else {
            return false;
        }
    }

    public UserData getUser(String username){
        return users.getOrDefault(username, null);
    }

    public UUID createGame(ChessGame game) {
        UUID gameID = UUID.randomUUID();
        games.put(gameID, game);
        return gameID;
    }

    public ChessGame getGame(UUID gameID) {
        return games.get(gameID);
    }

    public void clear() {
        users.clear();
        authTokens.clear();
        games.clear();
    }
}
