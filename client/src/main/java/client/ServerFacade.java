package client;

import client.requests.LoginRequest;
import client.requests.LogoutRequest;
import client.requests.RegisterRequest;
import client.responses.LoginResponse;
import client.responses.LogoutResponse;
import client.responses.RegisterResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class ServerFacade {

    private final int port;

    public ServerFacade(int port) {
        this.port = port;
    }

    private HttpURLConnection connect(String endpoint) throws IOException {
        URL url = URI.create("http://localhost:" + port + endpoint).toURL();
        return (HttpURLConnection) url.openConnection();
    }

    public LoginResponse login(String username, String password) throws IOException {
        // make connection
        HttpURLConnection connection = connect("/session");

        try {
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            LoginRequest request = new LoginRequest(username, password);
            String jsonRequest = new Gson().toJson(request);

            // Send request
            try (OutputStream output = connection.getOutputStream()) {
                output.write(jsonRequest.getBytes(StandardCharsets.UTF_8));
            }

            // Check response
            int responseCode = connection.getResponseCode();

            if (responseCode != 200) {
                throw new IOException("Login failed. HTTP status: " + responseCode);
            }

            // turn response into an actual object
            try (InputStreamReader reader = new InputStreamReader(
                    connection.getInputStream(), StandardCharsets.UTF_8)) {

                return new Gson().fromJson(reader, LoginResponse.class);
            }
        } finally {
            connection.disconnect();
        }
    }

    public LogoutResponse logout(String authToken) throws IOException {
        // make connection
        HttpURLConnection connection = connect("/session");

        try {
            connection.setRequestMethod("DELETE");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            LogoutRequest request = new LogoutRequest(authToken);
            String jsonRequest = new Gson().toJson(request);

            // Send request
            try (OutputStream output = connection.getOutputStream()) {
                output.write(jsonRequest.getBytes(StandardCharsets.UTF_8));
            }

            // Check response
            int responseCode = connection.getResponseCode();

            if (responseCode != 200) {
                throw new IOException("logout failed. HTTP status: " + responseCode);
            }

            // turn response into an actual object
            try (InputStreamReader reader = new InputStreamReader(
                    connection.getInputStream(), StandardCharsets.UTF_8)) {

                return new Gson().fromJson(reader, LogoutResponse.class);
            }
        } finally {
            connection.disconnect();
        }
    }

    public RegisterResponse register(String username, String Password) throws IOException {
// make connection
        HttpURLConnection connection = connect("/user");

        try {
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            RegisterRequest request = new RegisterRequest(username, Password);
            String jsonRequest = new Gson().toJson(request);

            // Send request
            try (OutputStream output = connection.getOutputStream()) {
                output.write(jsonRequest.getBytes(StandardCharsets.UTF_8));
            }

            // Check response
            int responseCode = connection.getResponseCode();

            if (responseCode != 200) {
                throw new IOException("Registration failed. HTTP status: " + responseCode);
            }

            // turn response into an actual object
            try (InputStreamReader reader = new InputStreamReader(
                    connection.getInputStream(), StandardCharsets.UTF_8)) {

                return new Gson().fromJson(reader, RegisterResponse.class);
            }
        } finally {
            connection.disconnect();
        }
    }
}
