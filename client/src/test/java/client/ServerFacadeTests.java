
package client;

import chess.ChessGame;
import client.responses.CreateGameResponse;
import client.responses.JoinGameResponse;
import client.responses.ListGamesResponse;
import client.responses.LoginResponse;
import client.responses.LogoutResponse;
import client.responses.RegisterResponse;
import org.junit.jupiter.api.*;
import server.Server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static int port;

    @BeforeAll
    public static void init() {
        server = new Server();
        port = server.run(0);
        facade = new ServerFacade(port);
    }

    @AfterAll
    public static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDatabase() throws Exception {
        HttpURLConnection connection = openConnection("/db", "DELETE");

        try {
            assertEquals(200, connection.getResponseCode());
        } finally {
            connection.disconnect();
        }
    }

    // Helper for test setup
    private static HttpURLConnection openConnection(
            String endpoint, String method) throws IOException {

        var url = URI.create(
                "http://localhost:" + port + endpoint).toURL();

        HttpURLConnection connection =
                (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(method);
        return connection;
    }

    // Registers a user directly through the server API.
    // This lets us test login independently of facade.register().
    private void registerTestUser() throws IOException {
        HttpURLConnection connection = openConnection("/user", "POST");

        try {
            connection.setDoOutput(true);
            connection.setRequestProperty(
                    "Content-Type", "application/json");

            String json = """
                    {"username":"testUser","password":"testPassword"}
                    """;

            try (OutputStream output = connection.getOutputStream()) {
                output.write(json.getBytes(StandardCharsets.UTF_8));
            }

            assertEquals(200, connection.getResponseCode());
        } finally {
            connection.disconnect();
        }
    }

    @Test
    public void loginSuccess() throws IOException {
        registerTestUser();

        LoginResponse response =
                facade.login("testUser", "testPassword");

        assertNotNull(response);
        assertEquals("testUser", response.getUsername());
        assertNotNull(response.getAuthToken());
        assertFalse(response.getAuthToken().isBlank());
    }

    @Test
    public void loginWrongPassword() throws IOException {
        registerTestUser();

        assertThrows(IOException.class, () ->
                facade.login("testUser", "wrongPassword"));
    }

    @Test
    public void loginNonexistentUser() {
        assertThrows(IOException.class, () ->
                facade.login("doesNotExist", "testPassword"));
    }

    @Test
    public void loginMissingPassword() throws IOException {
        registerTestUser();

        assertThrows(IOException.class, () ->
                facade.login("testUser", null));
    }

    @Test
    public void registerSuccess() throws IOException {
        RegisterResponse response =
                facade.register("testUser", "testPassword");

        assertNotNull(response);
        assertEquals("testUser", response.username());
        assertNotNull(response.authToken());
        assertFalse(response.authToken().isBlank());

        LoginResponse loginResponse =
                facade.login("testUser", "testPassword");

        assertEquals("testUser", loginResponse.getUsername());

        HttpURLConnection connection = openConnection("/game", "GET");
        connection.setRequestProperty("Authorization", response.authToken());

        try {
            assertEquals(200, connection.getResponseCode());
        } finally {
            connection.disconnect();
        }
    }

    @Test
    public void registerExistingUsername() throws IOException {
        registerTestUser();

        assertThrows(IOException.class, () ->
                facade.register("testUser", "anotherPassword"));
    }

    @Test
    public void registerMissingUsername() {
        assertThrows(IOException.class, () ->
                facade.register(null, "testPassword"));
    }

    @Test
    public void registerMissingPassword() {
        assertThrows(IOException.class, () ->
                facade.register("testUser", null));
    }

    @Test
    public void logoutSuccess() throws IOException {
        registerTestUser();

        LoginResponse loginResponse =
                facade.login("testUser", "testPassword");

        LogoutResponse response =
                facade.logout(loginResponse.getAuthToken());

        assertNotNull(response);

        HttpURLConnection connection = openConnection("/game", "GET");
        connection.setRequestProperty("Authorization", loginResponse.getAuthToken());

        try {
            assertEquals(401, connection.getResponseCode());
        } finally {
            connection.disconnect();
        }
    }

    @Test
    public void logoutInvalidToken() {
        assertThrows(IOException.class, () ->
                facade.logout("invalidToken"));
    }

    @Test
    public void logoutMissingToken() {
        assertThrows(IOException.class, () ->
                facade.logout(null));
    }

    @Test
    public void logoutAlreadyLoggedOut() throws IOException {
        registerTestUser();

        LoginResponse response =
                facade.login("testUser", "testPassword");

        facade.logout(response.getAuthToken());

        assertThrows(IOException.class, () ->
                facade.logout(response.getAuthToken()));
    }

    @Test
    public void createGameSuccess() throws IOException {
        RegisterResponse user = facade.register("testUser", "testPassword");

        CreateGameResponse response =
                facade.createGame(user.authToken(), "testGame");

        assertNotNull(response);
        assertTrue(response.getGameID() > 0);

        var games = facade.listGames(user.authToken()).getGames();

        assertEquals(1, games.size());
        assertEquals(response.getGameID(), games.get(0).getGameID());
        assertEquals("testGame", games.get(0).getGameName());
        assertNull(games.get(0).getWhiteUsername());
        assertNull(games.get(0).getBlackUsername());
    }

    @Test
    public void createGameInvalidToken() {
        assertThrows(IOException.class, () ->
                facade.createGame("invalidToken", "testGame"));
    }

    @Test
    public void createGameMissingName() throws IOException {
        RegisterResponse user = facade.register("testUser", "testPassword");

        assertThrows(IOException.class, () ->
                facade.createGame(user.authToken(), null));
    }

    @Test
    public void listGamesEmpty() throws IOException {
        RegisterResponse user = facade.register("testUser", "testPassword");

        ListGamesResponse response = facade.listGames(user.authToken());

        assertNotNull(response);
        assertNotNull(response.getGames());
        assertTrue(response.getGames().isEmpty());
    }

    @Test
    public void listGamesSuccess() throws IOException {
        RegisterResponse user = facade.register("testUser", "testPassword");
        CreateGameResponse first = facade.createGame(user.authToken(), "firstGame");
        CreateGameResponse second = facade.createGame(user.authToken(), "secondGame");
        facade.joinGame(user.authToken(), ChessGame.TeamColor.WHITE, first.getGameID());

        ListGamesResponse response = facade.listGames(user.authToken());

        assertNotNull(response);
        assertEquals(2, response.getGames().size());

        var firstGame = response.getGames().stream()
                .filter(game -> game.getGameID() == first.getGameID()).findFirst().orElseThrow();
        var secondGame = response.getGames().stream()
                .filter(game -> game.getGameID() == second.getGameID()).findFirst().orElseThrow();

        assertEquals("firstGame", firstGame.getGameName());
        assertEquals("testUser", firstGame.getWhiteUsername());
        assertNull(firstGame.getBlackUsername());
        assertEquals("secondGame", secondGame.getGameName());
        assertNull(secondGame.getWhiteUsername());
        assertNull(secondGame.getBlackUsername());
    }

    @Test
    public void listGamesInvalidToken() {
        assertThrows(IOException.class, () ->
                facade.listGames("invalidToken"));
    }

    @Test
    public void joinGameSuccess() throws IOException {
        RegisterResponse white = facade.register("whiteUser", "testPassword");
        RegisterResponse black = facade.register("blackUser", "testPassword");
        CreateGameResponse game = facade.createGame(white.authToken(), "testGame");

        JoinGameResponse whiteResponse =
                facade.joinGame(white.authToken(), ChessGame.TeamColor.WHITE, game.getGameID());
        JoinGameResponse blackResponse =
                facade.joinGame(black.authToken(), ChessGame.TeamColor.BLACK, game.getGameID());

        assertNotNull(whiteResponse);
        assertNotNull(blackResponse);

        var listedGame = facade.listGames(white.authToken()).getGames().get(0);

        assertEquals(game.getGameID(), listedGame.getGameID());
        assertEquals("whiteUser", listedGame.getWhiteUsername());
        assertEquals("blackUser", listedGame.getBlackUsername());
    }

    @Test
    public void joinGameInvalidToken() throws IOException {
        RegisterResponse user = facade.register("testUser", "testPassword");
        CreateGameResponse game = facade.createGame(user.authToken(), "testGame");

        assertThrows(IOException.class, () ->
                facade.joinGame("invalidToken", ChessGame.TeamColor.WHITE, game.getGameID()));

        var listedGame = facade.listGames(user.authToken()).getGames().get(0);
        assertNull(listedGame.getWhiteUsername());
    }

    @Test
    public void joinGameNonexistentGame() throws IOException {
        RegisterResponse user = facade.register("testUser", "testPassword");

        assertThrows(IOException.class, () ->
                facade.joinGame(user.authToken(), ChessGame.TeamColor.WHITE, -1));
    }

    @Test
    public void joinGameMissingColor() throws IOException {
        RegisterResponse user = facade.register("testUser", "testPassword");
        CreateGameResponse game = facade.createGame(user.authToken(), "testGame");

        assertThrows(IOException.class, () ->
                facade.joinGame(user.authToken(), null, game.getGameID()));
    }

    @Test
    public void joinGameMissingGameID() throws IOException {
        RegisterResponse user = facade.register("testUser", "testPassword");

        assertThrows(IOException.class, () ->
                facade.joinGame(user.authToken(), ChessGame.TeamColor.WHITE, null));
    }

    @Test
    public void joinGameColorTaken() throws IOException {
        RegisterResponse first = facade.register("firstUser", "testPassword");
        RegisterResponse second = facade.register("secondUser", "testPassword");
        CreateGameResponse game = facade.createGame(first.authToken(), "testGame");
        facade.joinGame(first.authToken(), ChessGame.TeamColor.WHITE, game.getGameID());

        assertThrows(IOException.class, () ->
                facade.joinGame(second.authToken(), ChessGame.TeamColor.WHITE, game.getGameID()));

        var listedGame = facade.listGames(first.authToken()).getGames().get(0);
        assertEquals("firstUser", listedGame.getWhiteUsername());
        assertNull(listedGame.getBlackUsername());
    }
}
