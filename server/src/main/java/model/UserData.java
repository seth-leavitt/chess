package model;

import dataaccess.DataAccessException;

public class UserData {
    public String getPassword() {
        return password;
    }

    String username;
    String password;
    String email;

    public UserData(String username, String password, String email) throws DataAccessException {
        if (username == null) {
            throw new DataAccessException("No Username Provided");
        }
        if (password == null) {
            throw new DataAccessException("No Password Provided");
        }
        if (email == null){
            throw new DataAccessException("No Email Provided!");
        }
        this.username = username;
        this.password = password;
        this.email = email;

    }

    public String getUsername(){
        return username;
    }
    public String getEmail(){
        return email;
    }
}


