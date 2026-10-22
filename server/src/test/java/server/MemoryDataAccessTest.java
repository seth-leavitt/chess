package server;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.UserData;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class MemoryDataAccessTest {
    private MemoryDataAccess da;
    @Test
    void clear(){
        var da = new MemoryDataAccess();
    }

    @BeforeEach
    void setup(){
        da = new MemoryDataAccess();
    }

    @Test
    void registerStoresUserAndReturnsToken() throws DataAccessException {

        UserData user = new UserData("seth", "password");

        String token = da.registerUser(user);

        UserData savedUser = da.getUser("seth");

        assertNotNull(savedUser);
        assertEquals("seth", savedUser.getUsername());
        assertEquals("password", savedUser.getPassword());

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertEquals(token, savedUser.getAuthToken());
    }

    @Test
    void getUser(){
    }
}
