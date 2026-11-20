package client;

import chess.ChessGame;
import client.requests.CreateGameRequest;
import client.requests.JoinGameRequest;
import client.requests.LoginRequest;
import client.requests.LogoutRequest;
import client.requests.RegisterRequest;
import client.responses.CreateGameResponse;
import client.responses.JoinGameResponse;
import client.responses.ListGamesResponse;
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
            connection.setRequestProperty("Authorization", authToken);

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

    public CreateGameResponse createGame(String authToken, String gameName) throws IOException {
        // make connection
        HttpURLConnection connection = connect("/game");

        try {
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", authToken);

            CreateGameRequest request = new CreateGameRequest(gameName);
            String jsonRequest = new Gson().toJson(request);

            // Send request
            try (OutputStream output = connection.getOutputStream()) {
                output.write(jsonRequest.getBytes(StandardCharsets.UTF_8));
            }

            // Check response
            int responseCode = connection.getResponseCode();

            if (responseCode != 200) {
                throw new IOException("Create game failed. HTTP status: " + responseCode);
            }

            // turn response into an actual object
            try (InputStreamReader reader = new InputStreamReader(
                    connection.getInputStream(), StandardCharsets.UTF_8)) {

                return new Gson().fromJson(reader, CreateGameResponse.class);
            }
        } finally {
            connection.disconnect();
        }
    }

    public ListGamesResponse listGames(String authToken) throws IOException {
        // make connection
        HttpURLConnection connection = connect("/game");

        try {
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", authToken);

            // Check response
            int responseCode = connection.getResponseCode();

            if (responseCode != 200) {
                throw new IOException("List games failed. HTTP status: " + responseCode);
            }

            // turn response into an actual object
            try (InputStreamReader reader = new InputStreamReader(
                    connection.getInputStream(), StandardCharsets.UTF_8)) {

                return new Gson().fromJson(reader, ListGamesResponse.class);
            }
        } finally {
            connection.disconnect();
        }
    }

    public JoinGameResponse joinGame(String authToken, ChessGame.TeamColor playerColor, Integer gameID) throws IOException {
        // make connection
        HttpURLConnection connection = connect("/game");

        try {
            connection.setRequestMethod("PUT");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", authToken);

            JoinGameRequest request = new JoinGameRequest(playerColor, gameID);
            String jsonRequest = new Gson().toJson(request);

            // Send request
            try (OutputStream output = connection.getOutputStream()) {
                output.write(jsonRequest.getBytes(StandardCharsets.UTF_8));
            }

            // Check response
            int responseCode = connection.getResponseCode();

            if (responseCode != 200) {
                throw new IOException("Join game failed. HTTP status: " + responseCode);
            }

            // turn response into an actual object
            try (InputStreamReader reader = new InputStreamReader(
                    connection.getInputStream(), StandardCharsets.UTF_8)) {

                return new Gson().fromJson(reader, JoinGameResponse.class);
            }
        } finally {
            connection.disconnect();
        }
    }

    public RegisterResponse register(String username, String password) throws IOException {
// make connection
        HttpURLConnection connection = connect("/user");

        try {
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            RegisterRequest request = new RegisterRequest(username, password);
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
