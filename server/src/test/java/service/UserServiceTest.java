package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.LoginRequest;
import requests.LogoutRequest;
import requests.RegistrationRequest;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private MemoryDataAccess dataAccess;
    private UserService service;

    @BeforeEach
    void setup() {
        dataAccess = new MemoryDataAccess();
        service = new UserService(dataAccess);
    }

    // REGISTRATION

    @Test
    void registerStoresUserAndReturnsToken() throws DataAccessException {
        RegistrationRequest request =
                new RegistrationRequest("username", "password");

        var result = service.register(request);

        assertEquals("username", result.getUsername());
        assertNotNull(result.getAuthToken());
        assertFalse(result.getAuthToken().isBlank());

        UserData savedUser = dataAccess.getUser("username");

        assertNotNull(savedUser);
        assertEquals("password", savedUser.getPassword());
        assertEquals(savedUser.getUsername(),
                dataAccess.getAuthByToken(result.getAuthToken()).getUsername());
    }

    @Test
    void registerDuplicateUsernameThrows() throws DataAccessException {
        dataAccess.registerUser(new UserData("username", "original"));

        assertThrows(DataAccessException.class, () -> {
            service.register(
                    new RegistrationRequest("username", "different"));
        });

        assertEquals(
                "original", dataAccess.getUser("username").getPassword());
    }

    @Test
    void registerMissingUsernameThrows() {
        RegistrationRequest request =
                new RegistrationRequest(null, "password");

        assertThrows(DataAccessException.class, () -> {
            service.register(request);
        });
    }

    @Test
    void registerMissingPasswordThrows() {
        RegistrationRequest request =
                new RegistrationRequest("username", null);

        assertThrows(DataAccessException.class, () -> {
            service.register(request);
        });
    }

    // LOGIN

    @Test
    void loginWithCorrectCredentialsSucceeds() throws DataAccessException {
        dataAccess.registerUser(new UserData("username", "password"));

        var result = service.login(
                new LoginRequest("username", "password"));

        assertEquals("username", result.getUsername());
        assertNotNull(result.getAuthToken());
        assertFalse(result.getAuthToken().isBlank());
    }

    @Test
    void loginWithWrongPasswordThrows() throws DataAccessException {
        dataAccess.registerUser(new UserData("username", "correct"));

        assertThrows(DataAccessException.class, () -> {
            service.login(new LoginRequest("username", "wrong"));
        });
    }

    @Test
    void loginWithUnknownUsernameThrows() {
        assertThrows(DataAccessException.class, () -> {
            service.login(new LoginRequest("nobody", "password"));
        });
    }

    @Test
    void eachLoginReturnsNewToken() throws DataAccessException {
        String registrationToken =
                dataAccess.registerUser(new UserData("username", "password")).getAuthToken();

        var firstLogin =
                service.login(new LoginRequest("username", "password"));

        var secondLogin =
                service.login(new LoginRequest("username", "password"));

        assertNotEquals(
                registrationToken, firstLogin.getAuthToken());

        assertNotEquals(
                registrationToken, secondLogin.getAuthToken());

        assertNotEquals(
                firstLogin.getAuthToken(), secondLogin.getAuthToken());
    }

    // LOGOUT

    @Test
    void logoutWithValidTokenSucceeds() throws DataAccessException {
        String token =
                dataAccess.registerUser(new UserData("username", "password")).getAuthToken();

        var result = service.logout(new LogoutRequest(token));

        assertTrue(result.getStatus());

        // Logging out should preserve the account.
        assertNotNull(dataAccess.getUser("username"));
    }

    @Test
    void logoutWithInvalidTokenReturnsFalse() throws DataAccessException {
        var result =
                service.logout(new LogoutRequest("invalid-token"));

        assertFalse(result.getStatus());
    }

    @Test
    void logoutTwiceFailsSecondTime() throws DataAccessException {
        String token =
                dataAccess.registerUser(new UserData("username", "password")).getAuthToken();

        var firstLogout = service.logout(new LogoutRequest(token));
        var secondLogout = service.logout(new LogoutRequest(token));

        assertTrue(firstLogout.getStatus());
        assertFalse(secondLogout.getStatus());
    }

    @Test
    void loggedOutUserCanCreateNewSession() throws DataAccessException {
        String originalToken =
                dataAccess.registerUser(new UserData("username", "password")).getAuthToken();

        assertTrue(
                service.logout(new LogoutRequest(originalToken))
                        .getStatus());

        var login =
                service.login(new LoginRequest("username", "password"));

        assertNotEquals(originalToken, login.getAuthToken());

        // The new token should identify a valid session.
        assertTrue(
                service.logout(new LogoutRequest(login.getAuthToken()))
                        .getStatus());
    }

    @Test
    void missingLoginFieldsAreBadRequests() {
        for (LoginRequest request : new LoginRequest[]{null,
                new LoginRequest(null, "password"), new LoginRequest("username", null)}) {
            ServiceException exception = assertThrows(ServiceException.class, () -> service.login(request));
            assertEquals(400, exception.getStatusCode());
        }
    }

    @Test
    void dataAccessFailuresAreNotReclassifiedAsClientErrors() throws DataAccessException {
        DataAccessException failure = new DataAccessException("Database unavailable");
        MemoryDataAccess failingDataAccess = new MemoryDataAccess() {
            @Override
            public AuthData registerUser(UserData user) throws DataAccessException {
                throw failure;
            }

            @Override
            public boolean logoutUser(String token) throws DataAccessException {
                throw failure;
            }
        };
        UserService failingService = new UserService(failingDataAccess);

        assertSame(failure, assertThrows(DataAccessException.class,
                () -> failingService.register(new RegistrationRequest("username", "password"))));
        assertSame(failure, assertThrows(DataAccessException.class,
                () -> failingService.logout(new LogoutRequest("token"))));
    }
}
