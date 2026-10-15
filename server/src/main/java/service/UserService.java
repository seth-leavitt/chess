package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.UserData;
import requests.RegistrationRequest;
import results.RegistrationResult;

public class UserService {
    DataAccess dataAccess;

    public UserService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public RegistrationResult register(RegistrationRequest request) throws DataAccessException{
        UserData user = new UserData(request.getUsername(), request.getPassword());
        try {
            dataAccess.RegisterUser(user);
        } catch (DataAccessException e) {
            throw new DataAccessException("User Registration Failed");
        }

    }
}
