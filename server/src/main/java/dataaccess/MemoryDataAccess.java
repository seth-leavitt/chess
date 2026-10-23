package dataaccess;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

public class MemoryDataAccess implements DataAccess {
    HashMap<String, UserData> users = new HashMap<>();
    HashMap<String, AuthData> authTokens = new HashMap<>();
    HashMap<Integer, GameData> games = new HashMap<>();
    private int nextGameID = 1;

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

    public AuthData getAuthByToken(String authToken){
        return authTokens.getOrDefault(authToken, null);
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

    public int addGame(String gameName, ChessGame game) {
        int gameID = nextGameID++;
        games.put(gameID, new GameData(gameID, gameName, null, null, game));
        return gameID;
    }

    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    public Collection<GameData> listGames() {
        return new ArrayList<>(games.values());
    }

    public void clear() {
        users.clear();
        authTokens.clear();
        games.clear();
    }
}
