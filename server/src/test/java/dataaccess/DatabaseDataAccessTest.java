package dataaccess;

import dataaccess.sql.DatabaseDataAccess;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseDataAccessTest {
    private DatabaseDataAccess da;

    @BeforeEach
    void setup() throws DataAccessException {
        da = new DatabaseDataAccess();
        da.clear();
    }

    @AfterEach
    void cleanup() throws DataAccessException {
        if (da != null) {
            da.clear();
        }
    }

    @Test
    void registerStoresUserAndReturnsToken() throws DataAccessException {
        UserData user = new UserData("username", "password");

        AuthData authData = da.registerUser(user);

        UserData savedUser = da.getUser("username");
        assertNotNull(savedUser);
        assertEquals("username", savedUser.getUsername());
        assertNotEquals("password", savedUser.getPassword());
        assertTrue(BCrypt.checkpw("password", savedUser.getPassword()));
        assertNotNull(authData);
        assertEquals("username", authData.getUsername());
        assertNotNull(authData.getAuthToken());
        assertFalse(authData.getAuthToken().isBlank());
        assertNotNull(da.getAuthByToken(authData.getAuthToken()));
    }

    @Test
    void registeringDuplicateUsernameThrows() throws DataAccessException {
        da.registerUser(new UserData("username", "original"));

        assertThrows(DataAccessException.class,
                () -> da.registerUser(new UserData("username", "different")));

        assertTrue(BCrypt.checkpw("original", da.getUser("username").getPassword()));
    }

    @Test
    void loginReturnsNewStoredToken() throws DataAccessException {
        UserData user = new UserData("username", "password");
        AuthData registration = da.registerUser(user);

        AuthData login = da.loginUser(user);

        assertNotNull(login);
        assertEquals("username", login.getUsername());
        assertNotNull(login.getAuthToken());
        assertFalse(login.getAuthToken().isBlank());
        assertNotEquals(registration.getAuthToken(), login.getAuthToken());
        AuthData savedAuth = da.getAuthByToken(login.getAuthToken());
        assertNotNull(savedAuth);
        assertEquals(login.getAuthToken(), savedAuth.getAuthToken());
        assertEquals(login.getUsername(), savedAuth.getUsername());
        assertNotNull(da.getAuthByToken(registration.getAuthToken()));
    }

    @Test
    void loginForUnknownUserThrows() {
        assertThrows(DataAccessException.class,
                () -> da.loginUser(new UserData("nobody", "password")));
    }

    @Test
    void getAuthByTokenReturnsCorrectAuth() throws DataAccessException {
        AuthData first = da.registerUser(new UserData("first", "password"));
        AuthData second = da.registerUser(new UserData("second", "password"));

        AuthData savedAuth = da.getAuthByToken(second.getAuthToken());

        assertNotNull(savedAuth);
        assertEquals(second.getAuthToken(), savedAuth.getAuthToken());
        assertEquals("second", savedAuth.getUsername());
        assertNotEquals(first.getAuthToken(), savedAuth.getAuthToken());
    }

    @Test
    void unknownTokenReturnsNull() throws DataAccessException {
        assertNull(da.getAuthByToken("invalid-token"));
        assertNull(da.getAuthByToken(null));
    }

    @Test
    void logoutRevokesOnlyRequestedToken() throws DataAccessException {
        UserData user = new UserData("username", "password");
        AuthData registration = da.registerUser(user);
        AuthData login = da.loginUser(user);

        assertTrue(da.logoutUser(registration.getAuthToken()));

        assertNull(da.getAuthByToken(registration.getAuthToken()));
        assertNotNull(da.getAuthByToken(login.getAuthToken()));
        assertNotNull(da.getUser("username"));
        assertFalse(da.logoutUser(registration.getAuthToken()));
    }

    @Test
    void logoutForUnknownTokenReturnsFalse() throws DataAccessException {
        assertFalse(da.logoutUser("invalid-token"));
        assertFalse(da.logoutUser(null));
    }

    @Test
    void getUserReturnsCorrectUser() throws DataAccessException {
        da.registerUser(new UserData("first", "first-password"));
        da.registerUser(new UserData("second", "second-password"));

        UserData savedUser = da.getUser("second");

        assertNotNull(savedUser);
        assertEquals("second", savedUser.getUsername());
        assertTrue(BCrypt.checkpw("second-password", savedUser.getPassword()));
    }

    @Test
    void unknownUsernameReturnsNull() throws DataAccessException {
        assertNull(da.getUser("nobody"));
        assertNull(da.getUser(null));
    }
}
