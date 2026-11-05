package dataaccess.sql;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

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

    @Test
    void addGameStoresChessStateAndReturnsDistinctIDs() throws DataAccessException, InvalidMoveException {
        ChessGame game = new ChessGame();
        game.makeMove(new ChessMove(new ChessPosition(2, 5), new ChessPosition(4, 5), null));

        int firstID = da.addGame("Test game", game);
        int secondID = da.addGame("Test game", new ChessGame());

        assertTrue(firstID > 0);
        assertTrue(secondID > 0);
        assertNotEquals(firstID, secondID);
        GameData savedGame = da.getGame(firstID);
        assertNotNull(savedGame);
        assertEquals(firstID, savedGame.getGameID());
        assertEquals("Test game", savedGame.getGameName());
        assertNull(savedGame.getWhiteUsername());
        assertNull(savedGame.getBlackUsername());
        assertEquals(game, savedGame.getGame());
        assertEquals(new ChessGame(), da.getGame(secondID).getGame());
    }

    @Test
    void addGameWithoutNameThrows() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> da.addGame(null, new ChessGame()));

        assertTrue(da.listGames().isEmpty());
    }

    @Test
    void unknownGameIDReturnsNull() throws DataAccessException {
        da.addGame("Existing game", new ChessGame());

        assertNull(da.getGame(-1));
    }

    @Test
    void updateGameStoresAllFieldsWithoutChangingOtherGames() throws DataAccessException {
        int gameID = da.addGame("Original game", new ChessGame());
        int otherID = da.addGame("Other game", new ChessGame());
        ChessGame game = new ChessGame();
        game.setTeamTurn(ChessGame.TeamColor.BLACK);

        da.updateGame(new GameData(gameID, "Updated game", "white", "black", game));

        GameData savedGame = da.getGame(gameID);
        assertNotNull(savedGame);
        assertEquals(gameID, savedGame.getGameID());
        assertEquals("Updated game", savedGame.getGameName());
        assertEquals("white", savedGame.getWhiteUsername());
        assertEquals("black", savedGame.getBlackUsername());
        assertEquals(game, savedGame.getGame());
        GameData otherGame = da.getGame(otherID);
        assertEquals("Other game", otherGame.getGameName());
        assertNull(otherGame.getWhiteUsername());
        assertNull(otherGame.getBlackUsername());
        assertEquals(new ChessGame(), otherGame.getGame());
    }

    @Test
    void updateGameWithoutNameThrowsAndDoesntChangeOthers() throws DataAccessException {
        int gameID = da.addGame("Original game", new ChessGame());

        assertThrows(DataAccessException.class,
                () -> da.updateGame(new GameData(gameID, null, "white", "black", new ChessGame())));

        GameData savedGame = da.getGame(gameID);
        assertEquals("Original game", savedGame.getGameName());
        assertNull(savedGame.getWhiteUsername());
        assertNull(savedGame.getBlackUsername());
    }

    @Test
    void listGamesReturnsEmptyCollectionWhenNoGamesExist() throws DataAccessException {
        var games = da.listGames();

        assertNotNull(games);
        assertTrue(games.isEmpty());
    }


    @Test
    void dataPersistsAcrossDAOInstances() throws DataAccessException {
        AuthData authData = da.registerUser(new UserData("username", "password"));
        int gameID = da.addGame("Persistent game", new ChessGame());

        DatabaseDataAccess other = new DatabaseDataAccess();

        assertTrue(BCrypt.checkpw("password", other.getUser("username").getPassword()));
        assertEquals("username", other.getAuthByToken(authData.getAuthToken()).getUsername());
        assertEquals("Persistent game", other.getGame(gameID).getGameName());
        assertEquals(new ChessGame(), other.getGame(gameID).getGame());
    }


    @Test
    void clearingEmptyDatabaseRepeatedlySucceeds() throws DataAccessException {
        da.clear();
        da.clear();

        assertTrue(da.listGames().isEmpty());
        assertNull(da.getUser("username"));
    }
}
