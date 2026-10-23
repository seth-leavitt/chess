package passoff.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class UserErrorAPITest {
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

    @ParameterizedTest
    @CsvSource({
            "/user, ''", "/session, ''",
            "/user, null", "/session, null",
            "/user, {", "/session, {",
            "/user, []", "/session, []",
            "/user, {}", "/session, {}"
    })
    void invalidRequestBodyReturnsJsonBadRequest(String path, String body) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        assertJsonError(client.send(request, HttpResponse.BodyHandlers.ofString()), 400);
    }

    @Test
    void missingAuthorizationReturnsJsonUnauthorized() throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + "/session"))
                .DELETE().build();

        assertJsonError(client.send(request, HttpResponse.BodyHandlers.ofString()), 401);
    }

    private void assertJsonError(HttpResponse<String> response, int statusCode) {
        assertEquals(statusCode, response.statusCode());
        assertTrue(response.headers().firstValue("Content-Type").orElse("")
                .startsWith("application/json"));
        JsonObject body = JsonParser.parseString(response.body()).getAsJsonObject();
        assertTrue(body.get("message").getAsString().contains("Error"));
        assertFalse(body.has("username"));
        assertFalse(body.has("authToken"));
    }
}
