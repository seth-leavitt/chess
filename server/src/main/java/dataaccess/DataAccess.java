package dataaccess;

import model.AuthData;
import model.UserData;

public interface DataAccess {
    String registerUser(UserData user) throws DataAccessException;
    String loginUser(UserData user) throws DataAccessException;
    boolean logoutUser(String authToken) throws DataAccessException;
    UserData getUser(String username);
}
