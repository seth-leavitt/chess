package server;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.UserData;
import org.junit.jupiter.api.Test;

public class MemoryDataAccessTest {
    @Test
    void clear(){
        var da = new MemoryDataAccess();
    }

    @Test
    void register(){
        var da = new MemoryDataAccess();
        UserData user = new UserData("test1", "test2");
        try {
            da.registerUser(user);
        } catch (DataAccessException e) {

        }
    }

    @Test
    void getUser(){
    }
}
