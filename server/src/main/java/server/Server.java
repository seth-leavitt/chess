package server;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
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
        javalin.post("/user", userHandler::RegisterUser);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
