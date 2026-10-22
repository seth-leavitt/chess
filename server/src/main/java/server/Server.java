package server;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import handlers.DatabaseHandler;
import handlers.UserHandler;
import io.javalin.*;
import service.DatabaseService;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {
        // construct all of the stuff we need
        // DAO
        DataAccess dataAccess = new MemoryDataAccess();

        // Services
        UserService userService = new UserService(dataAccess);
        DatabaseService databaseService = new DatabaseService(dataAccess);

        // Handlers
        UserHandler userHandler = new UserHandler(userService);
        DatabaseHandler databaseHandler = new DatabaseHandler(databaseService);

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.post("/user", userHandler::registerUser);
        javalin.post("/session", userHandler::loginUser);
        javalin.delete("/session", userHandler::logoutUser);
        javalin.delete("/db", databaseHandler::clear);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
