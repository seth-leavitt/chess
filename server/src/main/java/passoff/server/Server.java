package passoff.server;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import handlers.DatabaseHandler;
import handlers.GameHandler;
import handlers.UserHandler;
import io.javalin.*;
import service.DatabaseService;
import service.GameService;
import service.ServiceException;
import service.UserService;

import java.util.Map;

public class Server {

    private final Javalin javalin;

    public Server() {
        // construct all of the stuff we need
        // DAO
        DataAccess dataAccess = new MemoryDataAccess();

        // Services
        UserService userService = new UserService(dataAccess);
        DatabaseService databaseService = new DatabaseService(dataAccess);
        GameService gameService = new GameService(dataAccess);

        // Handlers
        UserHandler userHandler = new UserHandler(userService);
        DatabaseHandler databaseHandler = new DatabaseHandler(databaseService);
        GameHandler gameHandler = new GameHandler(gameService);

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        Gson serializer = new Gson();
        javalin.exception(ServiceException.class, (exception, context) ->
                context.status(exception.getStatusCode()).contentType("application/json")
                        .result(serializer.toJson(Map.of("message", "Error: " + exception.getMessage()))));
        javalin.exception(JsonParseException.class, (exception, context) ->
                context.status(400).contentType("application/json")
                        .result(serializer.toJson(Map.of("message", "Error: bad request"))));
        javalin.exception(DataAccessException.class, (exception, context) ->
                context.status(500).contentType("application/json")
                        .result(serializer.toJson(Map.of("message", "Error: internal passoff.server error"))));

        // Register your endpoints and exception handlers here.
        javalin.post("/user", userHandler::registerUser);
        javalin.post("/session", userHandler::loginUser);
        javalin.delete("/session", userHandler::logoutUser);
        javalin.delete("/db", databaseHandler::clear);
        javalin.post("/game", gameHandler::createGame);
        javalin.get("/game", gameHandler::listGames);
        javalin.put("/game", gameHandler::joinGame);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
