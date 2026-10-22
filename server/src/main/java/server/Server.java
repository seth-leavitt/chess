package server;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import handlers.UserHandler;
import io.javalin.*;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {
        // construct all of the stuff we need
        // DAO
        DataAccess dataAccess = new MemoryDataAccess();

        // Services
        UserService userService = new UserService(dataAccess);

        // Handlers
        UserHandler userHandler = new UserHandler(userService);


        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.post("/user", userHandler::registerUser);
        javalin.post("/session", userHandler::loginUser);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
