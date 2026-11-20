package client.requests;

public class RegisterRequest {
    String username;

    final public String getPassword() {
        return password;
    }

    final public String getUsername(){
        return username;
    }

    String password;
    String email;

    public RegisterRequest(String username, String password){
        this.password = password;
        this.username = username;
    }
}
