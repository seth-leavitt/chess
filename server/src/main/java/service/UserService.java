package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import requests.LoginRequest;
import requests.LogoutRequest;
import requests.RegistrationRequest;
import results.*;

import java.util.Objects;

public class UserService {
    DataAccess dataAccess;

    public UserService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public RegistrationResult register(RegistrationRequest request) throws DataAccessException{
        if (request == null || request.getUsername() == null || request.getPassword() == null) {
            throw new ServiceException(400, "Username and password are required");
        }
        if (dataAccess.getUser(request.getUsername()) != null) {
            throw new ServiceException(403, "Username already taken");
        }
        UserData user = new UserData(request.getUsername(), request.getPassword());
        AuthData authData = dataAccess.registerUser(user);
        return new RegistrationResult(authData);
    }

    public LoginResult login(LoginRequest request) throws DataAccessException{
        if (request == null || request.getUsername() == null || request.getPassword() == null) {
            throw new ServiceException(400, "Username and password are required");
        }
        UserData user = dataAccess.getUser(request.getUsername());
        if (user == null || !Objects.equals(user.getPassword(), request.getPassword())) {
            throw new ServiceException(401, "Unauthorized");
        }
        AuthData authData = dataAccess.loginUser(user);
        return new LoginResult(authData);
    }

    public LogoutResult logout(LogoutRequest request) throws DataAccessException{
        return new LogoutResult(dataAccess.logoutUser(request.getAuthToken()));
    }
}
