package server;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.ListGamesRequest;
import results.CreateGameResult;
import service.GameService;
import service.ServiceException;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    private MemoryDataAccess dataAccess;
    private GameService service;
    private AuthData authData;

    @BeforeEach
    void setup() throws DataAccessException {
        dataAccess = new MemoryDataAccess();
        service = new GameService(dataAccess);
        authData = dataAccess.registerUser(new UserData("username", "password"));
    }

    @Test
    void newGameUsesSessionAndStoresDistinctGames() throws DataAccessException {
        CreateGameRequest firstRequest = new CreateGameRequest(authData.getAuthToken(), "First game");
        CreateGameRequest secondRequest = new CreateGameRequest(authData.getAuthToken(), "Second game");

        CreateGameResult firstResult = service.newGame(firstRequest);
        CreateGameResult secondResult = service.newGame(secondRequest);

        int firstID = firstResult.getGameID();
        int secondID = secondResult.getGameID();

        assertEquals(1, firstID);
        assertEquals(firstID + 1, secondID);
        assertNotNull(dataAccess.getGame(firstID));
        assertNotNull(dataAccess.getGame(secondID));
        assertSame(authData, dataAccess.getAuthByToken(authData.getAuthToken()));
    }

    @Test
    void newGameRejectsMissingOrUnknownSession() {
        assertThrows(DataAccessException.class, () -> service.newGame(null));
        CreateGameRequest badRequest = new CreateGameRequest("invalid-token", "Test game");
        assertThrows(DataAccessException.class, () -> service.newGame(badRequest));
    }

    @Test
    void newGameRejectsRevokedSessionButAcceptsOtherSessions() throws DataAccessException {
        AuthData otherSession = dataAccess.loginUser(dataAccess.getUser("username"));
        dataAccess.logoutUser(authData.getAuthToken());

        CreateGameRequest firstRequest = new CreateGameRequest(authData.getAuthToken(), "First game");
        CreateGameRequest otherRequest = new CreateGameRequest(otherSession.getAuthToken(), "Other game");


        assertThrows(DataAccessException.class, () -> service.newGame(firstRequest));
        assertNotNull(service.newGame(otherRequest));
    }

    @Test
    void clearRevokesGameCreationAccess() {
        dataAccess.clear();

        CreateGameRequest request = new CreateGameRequest(authData.getAuthToken(), "Test game");

        assertThrows(DataAccessException.class, () -> service.newGame(request));
    }

    @Test
    void listGamesReturnsEmptyList() throws DataAccessException {
        assertTrue(service.listGames(new ListGamesRequest(authData.getAuthToken())).getGames().isEmpty());
    }

    @Test
    void listGamesIncludesOtherUsersGamesAndMetadata() throws DataAccessException {
        AuthData otherUser = dataAccess.registerUser(new UserData("other", "password"));
        int gameID = service.newGame(new CreateGameRequest(otherUser.getAuthToken(), "Other game")).getGameID();

        var games = service.listGames(new ListGamesRequest(authData.getAuthToken())).getGames();

        assertEquals(1, games.size());
        assertEquals(gameID, games.getFirst().getGameID());
        assertEquals("Other game", games.getFirst().getGameName());
        assertNull(games.getFirst().getWhiteUsername());
        assertNull(games.getFirst().getBlackUsername());
    }

    @Test
    void listGamesRejectsMissingUnknownAndRevokedSessions() throws DataAccessException {
        for (ListGamesRequest request : new ListGamesRequest[]{null,
                new ListGamesRequest(null), new ListGamesRequest("invalid-token")}) {
            assertEquals(401, assertThrows(ServiceException.class,
                    () -> service.listGames(request)).getStatusCode());
        }
        dataAccess.logoutUser(authData.getAuthToken());
        assertThrows(DataAccessException.class,
                () -> service.listGames(new ListGamesRequest(authData.getAuthToken())));
    }

    @Test
    void missingGameNameDoesNotCreateGame() {
        assertEquals(400, assertThrows(ServiceException.class,
                () -> service.newGame(new CreateGameRequest(authData.getAuthToken(), null))).getStatusCode());
        assertTrue(dataAccess.listGames().isEmpty());
    }

    @Test
    void joinPreservesGameAndOtherPlayer() throws DataAccessException {
        int gameID = service.newGame(new CreateGameRequest(authData.getAuthToken(), "Test game")).getGameID();
        ChessGame board = dataAccess.getGame(gameID).getGame();
        AuthData otherUser = dataAccess.registerUser(new UserData("other", "password"));

        service.joinGame(new JoinGameRequest(authData.getAuthToken(), ChessGame.TeamColor.WHITE, gameID));
        service.joinGame(new JoinGameRequest(otherUser.getAuthToken(), ChessGame.TeamColor.BLACK, gameID));

        var game = dataAccess.getGame(gameID);
        assertEquals("username", game.getWhiteUsername());
        assertEquals("other", game.getBlackUsername());
        assertEquals("Test game", game.getGameName());
        assertSame(board, game.getGame());
    }

    @Test
    void sameUserCanJoinBothColorsButOccupiedColorIsForbidden() throws DataAccessException {
        int gameID = service.newGame(new CreateGameRequest(authData.getAuthToken(), "Test game")).getGameID();
        for (ChessGame.TeamColor color : ChessGame.TeamColor.values()) {
            JoinGameRequest request = new JoinGameRequest(authData.getAuthToken(), color, gameID);
            service.joinGame(request);
            assertEquals(403, assertThrows(ServiceException.class, () -> service.joinGame(request)).getStatusCode());
        }
        assertEquals("username", dataAccess.getGame(gameID).getWhiteUsername());
        assertEquals("username", dataAccess.getGame(gameID).getBlackUsername());
    }

    @Test
    void joinRejectsInvalidGameIDsAndMissingColor() throws DataAccessException {
        int gameID = service.newGame(new CreateGameRequest(authData.getAuthToken(), "Test game")).getGameID();
        for (Integer invalidID : new Integer[]{null, 0, -1, gameID + 1}) {
            JoinGameRequest request = new JoinGameRequest(authData.getAuthToken(), ChessGame.TeamColor.WHITE, invalidID);
            assertEquals(400, assertThrows(ServiceException.class, () -> service.joinGame(request)).getStatusCode());
        }
        assertEquals(400, assertThrows(ServiceException.class,
                () -> service.joinGame(new JoinGameRequest(authData.getAuthToken(), null, gameID))).getStatusCode());
        assertNull(dataAccess.getGame(gameID).getWhiteUsername());
        assertNull(dataAccess.getGame(gameID).getBlackUsername());
    }

    @Test
    void joinRejectsMissingUnknownAndRevokedSessions() throws DataAccessException {
        int gameID = service.newGame(new CreateGameRequest(authData.getAuthToken(), "Test game")).getGameID();
        dataAccess.logoutUser(authData.getAuthToken());
        for (JoinGameRequest request : new JoinGameRequest[]{null,
                new JoinGameRequest(null, ChessGame.TeamColor.WHITE, gameID),
                new JoinGameRequest("invalid-token", ChessGame.TeamColor.WHITE, gameID),
                new JoinGameRequest(authData.getAuthToken(), ChessGame.TeamColor.WHITE, gameID)}) {
            assertEquals(401, assertThrows(ServiceException.class, () -> service.joinGame(request)).getStatusCode());
        }
        assertNull(dataAccess.getGame(gameID).getWhiteUsername());
    }
}
