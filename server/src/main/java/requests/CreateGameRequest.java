package requests;

public class CreateGameRequest {
    String authToken;

    public CreateGameRequest(String authToken){
        this.authToken = authToken;
    }

    public String getAuthToken(){
        return authToken;
    }
}
