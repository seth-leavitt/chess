package requests;

public class ListGamesRequest {
    final String authToken;

    public ListGamesRequest(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }
}
