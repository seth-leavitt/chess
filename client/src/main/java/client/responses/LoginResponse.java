package client.responses;

public class LoginResponse {

    String username;

    final public String getAuthToken() {
        return authToken;
    }

    final public String getUsername(){
        return username;
    }

    String authToken;

    LoginResponse(String authToken, String username){
        this.authToken = authToken;
        this.username = username;
    }
}

