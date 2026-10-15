package server;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import requests.RegistrationRequest;
import service.UserService;
import com.google.gson.Gson;

import java.util.Map;

public class UserHandler {
    Gson serializer = new Gson();
    private final UserService userService;

    public UserHandler(UserService userService){
        this.userService = userService;
    }

    public void RegisterUser(Context context){
        RegistrationRequest request = serializer.fromJson(context.body(), RegistrationRequest.class);
        try {
            this.userService.register(request);
        } catch (DataAccessException e) {
            context.status(500).result(serializer.toJson(
                    Map.of("message", "Error: " + e.getMessage())
            ));
        }
    }
}
