package dataaccess;

import java.util.HashMap;
import java.util.UUID;

import model.UserData;

public class MemoryDataAccess implements DataAccess {
    HashMap<String, UserData> users = new HashMap<>();

    public String registerUser(UserData userData) throws DataAccessException {
        if (getUser(userData.getUsername()) != null){
            throw new DataAccessException("User Already Exists");
        }
        users.put(userData.getUsername(), userData);
        String authToken = String.valueOf(UUID.randomUUID());
        userData.setAuthToken(authToken);
        // now that the user is registered and we've save their token, we can pass the token back
        return authToken;
    }

    public UserData getUser(String username){
        return users.get(username);
    }
}
