package results;

import requests.RegistrationRequest;

public class RegistrationResult {
    final String username;
    final String authToken;
    public RegistrationResult(String username, String authToken){
        this.username = username;
        this.authToken = authToken;
    }
}
