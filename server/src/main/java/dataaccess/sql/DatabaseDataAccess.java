package dataaccess.sql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class DatabaseDataAccess implements DataAccess {

    public DatabaseDataAccess() throws DataAccessException {
        configureDatabase();
    }

    private void configureDatabase() throws DataAccessException {
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
        } catch (SQLException e) {
            throw new DataAccessException("failed to configure database", e);
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
            throw new DataAccessException("failes to get authData from database" , e);
        }
    }

    @Override
    public boolean logoutUser(String authToken) throws DataAccessException {
        var statement = "DELETE FROM auths WHERE authToken = ?";

        try (var connection = DatabaseManager.getConnection();
             var preparedStatement = connection.prepareStatement(statement)) {

            preparedStatement.setString(1, authToken);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DataAccessException("failed to delete authentication token", ex);
        }
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
    public int addGame(String gameName, ChessGame game) throws DataAccessException {
        var statement = "INSERT INTO games (gameName, game) VALUES (?, ?)";

        try (var connection = DatabaseManager.getConnection();
             var preparedStatement = connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, gameName);
            preparedStatement.setString(2, new Gson().toJson(game));
            preparedStatement.executeUpdate();

            try (var rs = preparedStatement.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new DataAccessException("failed to get generated game ID");
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to add game to database", ex);
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        var statement = "SELECT gameID, gameName, whiteUsername, blackUsername, game FROM games WHERE gameID = ?";

        try (var connection = DatabaseManager.getConnection();
             var preparedStatement = connection.prepareStatement(statement)) {

            preparedStatement.setInt(1, gameID);

            try (var rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return new GameData(
                            rs.getInt("gameID"),
                            rs.getString("gameName"),
                            rs.getString("whiteUsername"),
                            rs.getString("blackUsername"),
                            new Gson().fromJson(rs.getString("game"), ChessGame.class));
                }
                return null;
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to get game from database", ex);
        }
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        var statement = "UPDATE games SET gameName = ?, whiteUsername = ?, blackUsername = ?, game = ? WHERE gameID = ?";

        try (var connection = DatabaseManager.getConnection();
             var preparedStatement = connection.prepareStatement(statement)) {

            preparedStatement.setString(1, game.getGameName());
            preparedStatement.setString(2, game.getWhiteUsername());
            preparedStatement.setString(3, game.getBlackUsername());
            preparedStatement.setString(4, new Gson().toJson(game.getGame()));
            preparedStatement.setInt(5, game.getGameID());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to update game in database", ex);
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        var statement = "SELECT gameID, gameName, whiteUsername, blackUsername, game FROM games";

        try (var connection = DatabaseManager.getConnection();
             var preparedStatement = connection.prepareStatement(statement)) {

            Collection<GameData> games = new ArrayList<>();
            try (var rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    games.add(new GameData(
                            rs.getInt("gameID"),
                            rs.getString("gameName"),
                            rs.getString("whiteUsername"),
                            rs.getString("blackUsername"),
                            new Gson().fromJson(rs.getString("game"), ChessGame.class)));
                }
            }
            return games;
        } catch (SQLException ex) {
            throw new DataAccessException("failed to list games from database", ex);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "DELETE FROM auths";
        var statement2 = "DELETE FROM games";
        var statement3 = "DELETE FROM users";

        try (var connection = DatabaseManager.getConnection();
             var preparedStatement1 = connection.prepareStatement(statement);
             var preparedStatement2 = connection.prepareStatement(statement2);
             var preparedStatement3 = connection.prepareStatement(statement3)) {

            connection.setAutoCommit(false);
            try {
                preparedStatement1.executeUpdate();
                preparedStatement2.executeUpdate();
                preparedStatement3.executeUpdate();
                connection.commit();
            } catch (SQLException ex) {
                connection.rollback();
                throw ex;
            }
        } catch (SQLException ex) {
            throw new DataAccessException("failed to clear database", ex);
        }
    }
}
