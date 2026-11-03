package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class DatabaseDataAccess implements DataAccess{

    public DatabaseDataAccess() throws DataAccessException {
        configureDatabase();
    }

    private void configureDatabase() {
        var statement = """
            CREATE TABLE IF NOT EXISTS users (
                username VARCHAR(255) PRIMARY KEY,
                password VARCHAR(255) NOT NULL
            )
            """;

        var statement2 = """
            CREATE TABLE IF NOT EXISTS games (
                gameID INT AUTO_INCREMENT PRIMARY KEY,
                gameName VARCHAR(255) NOT NULL,
                whiteUsername VARCHAR(255),
                blackUsername VARCHAR(255),
                game TEXT NOT NULL
            )
            """;

        var statement3 = """
            CREATE TABLE IF NOT EXISTS auths (
                authToken VARCHAR(255) PRIMARY KEY,
                username VARCHAR(255) NOT NULL
            )
            """;

        try {
            DatabaseManager.createDatabase();

            try (var connection = DatabaseManager.getConnection();
                 var preparedStatement1 = connection.prepareStatement(statement);
                 var preparedStatement2 = connection.prepareStatement(statement2);
                 var preparedStatement3 = connection.prepareStatement(statement3)) {

                preparedStatement1.executeUpdate();
                preparedStatement2.executeUpdate();
                preparedStatement3.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("failed to configure database", e);
        }
    }

    @Override
    public AuthData registerUser(UserData user) throws DataAccessException {
        String clearTextPassword = user.getPassword();
        String username = user.getUsername();

        DatabaseManager.storeUserPassword(username, clearTextPassword);

        return loginUser(user);
    }

    @Override
    public AuthData loginUser(UserData user) throws DataAccessException {
        if (getUser(user.getUsername()) == null) {
            throw new DataAccessException("User does not exist");
        }

        AuthData authData = new AuthData(
                UUID.randomUUID().toString(), user.getUsername());

        var statement = "INSERT INTO auths (authToken, username) VALUES (?, ?)";

        try (var connection = DatabaseManager.getConnection();
             var preparedStatement = connection.prepareStatement(statement)) {

            preparedStatement.setString(1, authData.getAuthToken());
            preparedStatement.setString(2, user.getUsername());
            preparedStatement.executeUpdate();

            return authData;
        } catch (SQLException ex) {
            throw new DataAccessException("failed to store authentication token", ex);
        }
    }

    @Override
    public AuthData getAuthByToken(String authToken) throws DataAccessException {
        var statement = "SELECT authToken, username FROM auths WHERE authToken = ?";
        try (var connection = DatabaseManager.getConnection();
             var preparedStatement = connection.prepareStatement(statement)){
            preparedStatement.setString(1, authToken);

            try (var rs = preparedStatement.executeQuery()){
                if (rs.next()){
                    return new AuthData(rs.getString("authToken"), rs.getString("username"));
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("failes to get authData from database" , e);
        }
    }

    @Override
    public boolean logoutUser(String authToken) throws DataAccessException {
        return false;
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        var statement = "SELECT username, password FROM users WHERE username = ?";

        try (var connection = DatabaseManager.getConnection();
             var preparedStatement = connection.prepareStatement(statement)) {

            preparedStatement.setString(1, username);

            try (var rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return new UserData(
                            rs.getString("username"),
                            rs.getString("password"));
                }
                return null;
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to get user from database", ex);
        }
    }

    @Override
    public int addGame(String gameName, ChessGame game) {
        return 0;
    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public void updateGame(GameData game) {

    }

    @Override
    public Collection<GameData> listGames() {
        return List.of();
    }

    @Override
    public void clear() {

    }
}
