package handlers;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import requests.*;
import results.LoginResult;
import results.LogoutResult;
import results.RegistrationResult;
import service.ServiceException;
import service.UserService;
import com.google.gson.Gson;

public class UserHandler {
    Gson serializer = new Gson();
    private final UserService userService;

    public UserHandler(UserService userService){
        this.userService = userService;
    }

    public void registerUser(Context context) throws DataAccessException {
        RegistrationRequest request = serializer.fromJson(context.body(), RegistrationRequest.class);
        RegistrationResult result = this.userService.register(request);
        context.contentType("application/json");
        context.status(200).result(serializer.toJson(result));
    }

    public void loginUser(Context context) throws DataAccessException {
        LoginRequest request = serializer.fromJson(context.body(), LoginRequest.class);
        LoginResult result = this.userService.login(request);
        context.contentType("application/json");
        context.status(200).result(serializer.toJson(result));
    }

    public void logoutUser(Context context) throws DataAccessException {
        String authToken = context.header("Authorization");
        LogoutRequest request = new LogoutRequest(authToken);
        LogoutResult result = this.userService.logout(request);
        if (!result.getStatus()) {
            throw new ServiceException(401, "Session does not exist");
        }
        context.contentType("application/json");
        context.status(200).result(serializer.toJson(result));
    }
}
