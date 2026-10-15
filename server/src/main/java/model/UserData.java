package model;

import results.RegistrationResult;

public class UserData {
    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    String username;
    String password;
    String authToken;

    public UserData(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername(){
        return username;
    }
}
