package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
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
        UserData user = new UserData(request.getUsername(), request.getPassword());
        try {
            String authToken = dataAccess.registerUser(user);
            return new RegistrationResult(request.getUsername(), authToken);
        } catch (DataAccessException e) {
            throw new DataAccessException("User Registration Failed");
        }

    }

    public LoginResult login(LoginRequest request) throws DataAccessException{
        try {
            UserData user = dataAccess.getUser(request.getUsername());
            if (user == null) {
                throw new DataAccessException("User does not exist");
            }
            String realPassword = user.getPassword();
            String providedPassword = request.getPassword();
            if (Objects.equals(realPassword, providedPassword)){
                dataAccess.loginUser(user);

                // this is mildly messy, but we need to update the authToken
                user = dataAccess.getUser(request.getUsername());
                return new LoginResult(user.getUsername(), user.getAuthToken());
            } else {
                throw new DataAccessException("Incorrect Password");
            }
        }catch(DataAccessException e){
            throw new DataAccessException("Login Failed");
        }
    }

    public LogoutResult logout(LogoutRequest request) throws DataAccessException{
        try {
            String authToken = request.getAuthToken();
            return new LogoutResult(dataAccess.logoutUser(authToken));
        }catch(DataAccessException e){
            throw new DataAccessException("Session does not exist");
        }
    }
}
