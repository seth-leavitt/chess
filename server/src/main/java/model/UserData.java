package model;


public class UserData {
    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }


    public String getPassword() {
        return password;
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
