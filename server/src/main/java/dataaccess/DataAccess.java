package dataaccess;

import model.UserData;

public interface DataAccess {
    String registerUser(UserData user) throws DataAccessException;
    UserData getUser(String username);
}
