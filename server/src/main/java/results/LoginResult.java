package results;

public class LoginResult {
    final String username;
    final String authToken;
    public LoginResult(String username, String authToken){
        this.username = username;
        this.authToken = authToken;
    }
}
