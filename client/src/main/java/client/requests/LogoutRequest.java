package client.requests;

public class LogoutRequest {
    String authToken;

    public LogoutRequest(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

}
