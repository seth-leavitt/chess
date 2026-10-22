package results;

import model.AuthData;

public class RegistrationResult {
    final String username;
    final String authToken;
    public RegistrationResult(AuthData authData){
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
