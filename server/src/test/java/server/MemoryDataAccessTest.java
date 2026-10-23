package server;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class MemoryDataAccessTest {
    private MemoryDataAccess da;
    @Test
    void clear() throws DataAccessException {
        AuthData authData = da.registerUser(new UserData("username", "password"));
        var gameID = da.addGame(new ChessGame());

        da.clear();

        assertNull(da.getUser("username"));
        assertNull(da.getAuthByToken(authData.getAuthToken()));
        assertNull(da.getGame(gameID));
        assertNotNull(da.registerUser(new UserData("username", "password")));
    }

    @BeforeEach
    void setup(){
        da = new MemoryDataAccess();
    }

    @Test
    void registerStoresUserAndReturnsToken() throws DataAccessException {

        UserData user = new UserData("username", "password");

        AuthData authData = da.registerUser(user);

        UserData savedUser = da.getUser("username");

        assertNotNull(savedUser);
        assertEquals("username", savedUser.getUsername());
        assertEquals("password", savedUser.getPassword());

        assertNotNull(authData);
        assertFalse(authData.getAuthToken().isBlank());
        assertEquals(savedUser.getUsername(), authData.getUsername());
        assertSame(authData, da.getAuthByToken(authData.getAuthToken()));
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

    @Test
    void sessionsRemainIndependentAndLogoutRevokesOnlyOne() throws DataAccessException {
        UserData user = new UserData("username", "password");
        AuthData registration = da.registerUser(user);
        AuthData login = da.loginUser(user);

        assertNotEquals(registration.getAuthToken(), login.getAuthToken());
        assertEquals("username", login.getUsername());
        assertSame(registration, da.getAuthByToken(registration.getAuthToken()));
        assertSame(login, da.getAuthByToken(login.getAuthToken()));

        assertTrue(da.logoutUser(registration.getAuthToken()));
        assertNull(da.getAuthByToken(registration.getAuthToken()));
        assertSame(login, da.getAuthByToken(login.getAuthToken()));
        assertSame(user, da.getUser("username"));
        assertFalse(da.logoutUser(registration.getAuthToken()));
    }

    @Test
    void loginForUnknownUserThrows() {
        assertThrows(DataAccessException.class,
                () -> da.loginUser(new UserData("nobody", "password")));
    }

    @Test
    void unknownTokenReturnsNull() {
        assertNull(da.getAuthByToken("invalid-token"));
        assertNull(da.getAuthByToken(null));
    }
}
