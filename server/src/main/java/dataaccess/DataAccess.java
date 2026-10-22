package dataaccess;

import model.AuthData;
import model.UserData;

public interface DataAccess {
    AuthData registerUser(UserData user) throws DataAccessException;
    AuthData loginUser(UserData user) throws DataAccessException;
    AuthData getAuth(String authToken);
    boolean logoutUser(String authToken) throws DataAccessException;
    UserData getUser(String username);
}
