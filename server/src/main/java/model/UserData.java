package model;

import dataaccess.DataAccessException;

public class UserData {
    public String getPassword() {
        return password;
    }

    String username;
    String password;

    public UserData(String username, String password) throws DataAccessException {
        if (username == null) {
            throw new DataAccessException("No Username Provided");
        }
        if (password == null) {
            throw new DataAccessException("No Password Provided");
        }
        this.username = username;
        this.password = password;
    }

    public String getUsername(){
        return username;
    }
}
