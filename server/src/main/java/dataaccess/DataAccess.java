package dataaccess;

import model.UserData;

public interface DataAccess {
    String RegisterUser(UserData user) throws DataAccessException;
    UserData getUser(String username);
}
