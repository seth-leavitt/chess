
package client;

import client.responses.LoginResponse;
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
}
