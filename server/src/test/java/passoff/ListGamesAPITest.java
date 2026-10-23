package passoff.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import server.Server;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class ListGamesAPITest {
    private static Server server;
    private static String baseUrl;
    private static HttpClient client;

    @BeforeAll
    static void startServer() {
        server = new Server();
        baseUrl = "http://localhost:" + server.run(0);
        client = HttpClient.newHttpClient();
    }

    @AfterAll
    static void stopServer() {
        client.close();
        server.stop();
    }

    @BeforeEach
    void clear() throws Exception {
        assertEquals(200, send("DELETE", "/db", null, null).statusCode());
    }

    @Test
    void listReturnsAllGamesAsSummaries() throws Exception {
        String firstToken = register("first");
        String secondToken = register("second");
        assertEquals(200, send("POST", "/game", "{\"gameName\":\"First game\"}", firstToken).statusCode());
        assertEquals(200, send("POST", "/game", "{\"gameName\":\"Second game\"}", secondToken).statusCode());

        var response = send("GET", "/game", null, firstToken);

        assertEquals(200, response.statusCode());
        assertTrue(response.headers().firstValue("Content-Type").orElse("").startsWith("application/json"));
        var games = JsonParser.parseString(response.body()).getAsJsonObject().getAsJsonArray("games");
        assertEquals(2, games.size());
        var names = new java.util.HashSet<String>();
        for (var entry : games) {
            JsonObject game = entry.getAsJsonObject();
            assertTrue(game.get("gameID").getAsJsonPrimitive().isNumber());
            assertTrue(game.get("gameID").getAsInt() > 0);
            names.add(game.get("gameName").getAsString());
            assertFalse(game.has("game"));
            assertFalse(game.has("authToken"));
            assertTrue(!game.has("whiteUsername") || game.get("whiteUsername").isJsonNull());
            assertTrue(!game.has("blackUsername") || game.get("blackUsername").isJsonNull());
        }
        assertEquals(java.util.Set.of("First game", "Second game"), names);
    }

    @Test
    void listRejectsMissingUnknownRevokedAndClearedTokens() throws Exception {
        String token = register("user");
        assertEquals(200, send("DELETE", "/session", null, token).statusCode());
        String clearedToken = register("other");
        assertEquals(200, send("DELETE", "/db", null, null).statusCode());

        for (String authToken : new String[]{null, "invalid-token", token, clearedToken}) {
            var response = send("GET", "/game", null, authToken);
            assertEquals(401, response.statusCode());
            JsonObject error = JsonParser.parseString(response.body()).getAsJsonObject();
            assertTrue(error.get("message").getAsString().contains("Error"));
            assertFalse(error.has("games"));
        }
    }

    @Test
    void clearRemovesGamesFromList() throws Exception {
        String token = register("user");
        assertEquals(200, send("POST", "/game", "{\"gameName\":\"Old game\"}", token).statusCode());
        assertEquals(200, send("DELETE", "/db", null, null).statusCode());
        String newToken = register("user");

        var response = send("GET", "/game", null, newToken);

        assertEquals(200, response.statusCode());
        assertTrue(JsonParser.parseString(response.body()).getAsJsonObject().getAsJsonArray("games").isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "null", "{", "[]", "{}", "{\"playerColor\":\"WHITE\"}",
            "{\"gameID\":1}", "{\"gameID\":\"invalid\",\"playerColor\":\"WHITE\"}"})
    void invalidJoinBodyReturnsJsonBadRequest(String body) throws Exception {
        String token = register("user");

        var response = send("PUT", "/game", body, token);

        assertEquals(400, response.statusCode());
        assertTrue(response.headers().firstValue("Content-Type").orElse("").startsWith("application/json"));
        assertTrue(JsonParser.parseString(response.body()).getAsJsonObject().get("message").getAsString().contains("Error"));
    }

    @Test
    void joinUsesHeaderIdentityAndUpdatesList() throws Exception {
        String token = register("user");
        String otherToken = register("other");
        var creation = send("POST", "/game", "{\"gameName\":\"Test game\"}", token);
        assertEquals(200, creation.statusCode());
        int gameID = JsonParser.parseString(creation.body()).getAsJsonObject().get("gameID").getAsInt();
        String body = "{\"gameID\":" + gameID + ",\"playerColor\":\"BLACK\",\"authToken\":\"" + otherToken + "\"}";

        assertEquals(401, send("PUT", "/game", body, null).statusCode());
        var response = send("PUT", "/game", body, token);

        assertEquals(200, response.statusCode());
        assertTrue(response.headers().firstValue("Content-Type").orElse("").startsWith("application/json"));
        assertEquals(new JsonObject(), JsonParser.parseString(response.body()));
        var listing = send("GET", "/game", null, token);
        JsonObject game = JsonParser.parseString(listing.body()).getAsJsonObject()
                .getAsJsonArray("games").get(0).getAsJsonObject();
        assertEquals("user", game.get("blackUsername").getAsString());
    }

    private String register(String username) throws Exception {
        String body = "{\"username\":\"" + username + "\",\"password\":\"password\"}";
        var response = send("POST", "/user", body, null);
        assertEquals(200, response.statusCode());
        return JsonParser.parseString(response.body()).getAsJsonObject().get("authToken").getAsString();
    }

    private HttpResponse<String> send(String method, String path, String body, String authToken) throws Exception {
        var request = HttpRequest.newBuilder(URI.create(baseUrl + path)).header("Content-Type", "application/json");
        if (authToken != null) {
            request.header("Authorization", authToken);
        }
        request.method(method, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body));
        return client.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }
}
