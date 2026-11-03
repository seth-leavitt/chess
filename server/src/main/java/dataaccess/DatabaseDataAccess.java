package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class DatabaseDataAccess implements DataAccess{
    DatabaseManager sqlDB = new DatabaseManager();

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
        return null;
    }

    @Override
    public AuthData loginUser(UserData user) throws DataAccessException {
        return null;
    }

    @Override
    public AuthData getAuthByToken(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public boolean logoutUser(String authToken) throws DataAccessException {
        return false;
    }

    @Override
    public UserData getUser(String username) {
        return null;
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
