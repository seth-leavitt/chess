package handlers;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import requests.RegistrationRequest;
import results.RegistrationResult;
import service.UserService;
import com.google.gson.Gson;

import java.util.Map;

public class UserHandler {
    Gson serializer = new Gson();
    private final UserService userService;

    public UserHandler(UserService userService){
        this.userService = userService;
    }

    public void registerUser(Context context){
        RegistrationRequest request = serializer.fromJson(context.body(), RegistrationRequest.class);
        try {
            RegistrationResult result = this.userService.register(request);
            context.contentType("application/json");
            context.status(200).result(serializer.toJson(result));
        } catch (DataAccessException e) {
            context.status(500).result(serializer.toJson(
                    Map.of("message", "Error: " + e.getMessage())
            ));
        }

    }
}
