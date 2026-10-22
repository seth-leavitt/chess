package results;

import model.AuthData;

public class LoginResult {
    final String username;
    final String authToken;
    public LoginResult(AuthData authData){
        this.username = authData.getUsername();
        this.authToken = authData.getAuthToken();
    }

    public String getUsername() {
        return username;
    }

    public String getAuthToken() {
        return authToken;
    }
}
