package dataaccess;

import java.util.HashMap;
import java.util.UUID;

import model.UserData;

public class MemoryDataAccess implements DataAccess {
    HashMap<String, UserData> users = new HashMap<>();
    HashMap<String, UserData> authTokens = new HashMap<>();

    public String registerUser(UserData userData) throws DataAccessException {
        if (getUser(userData.getUsername()) != null){
            throw new DataAccessException("User Already Exists");
        }
        String authToken = String.valueOf(UUID.randomUUID());
        userData.setAuthToken(authToken);
        users.put(userData.getUsername(), userData);
        authTokens.put(userData.getAuthToken(), userData);
        // now that the user is registered and we've save their token, we can pass the token back
        return authToken;
    }

    public String loginUser(UserData userData) throws DataAccessException {
        if (getUser(userData.getUsername()) == null){
            throw new DataAccessException("User does not exists");
        }
        String authToken = String.valueOf(UUID.randomUUID());
        userData.setAuthToken(authToken);
        // now that the user is registered and we've save their token, we can pass the token back
        return authToken;
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
}
