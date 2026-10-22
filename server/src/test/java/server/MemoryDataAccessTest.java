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

        UserData user = new UserData("username", "password");

        String token = da.registerUser(user);

        UserData savedUser = da.getUser("username");

        assertNotNull(savedUser);
        assertEquals("username", savedUser.getUsername());
        assertEquals("password", savedUser.getPassword());

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertEquals(token, savedUser.getAuthToken());
    }
    @Test
    void registeringDuplicateUsernameThrows() throws DataAccessException {
        // Arrange: this username already exists
        da.registerUser(new UserData("username", "original"));

        // Act + Assert
        assertThrows(DataAccessException.class, () -> {
            da.registerUser(new UserData("username", "different"));
        });

        // The failed registration should preserve the original user
        assertEquals("original", da.getUser("username").getPassword());
    }

    @Test
    void getUserReturnsCorrectUser() throws DataAccessException {
        // Arrange
        da.registerUser(new UserData("username", "first"));
        da.registerUser(new UserData("username2", "second"));

        // Act
        UserData user = da.getUser("username2");

        // Assert
        assertNotNull(user);
        assertEquals("username2", user.getUsername());
        assertEquals("second", user.getPassword());
    }

    @Test
    void unknownUsernameReturnsNull() {
        UserData user = da.getUser("nobody");

        assertNull(user);
    }
}
