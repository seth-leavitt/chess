package client.requests;

public class RegisterRequest {
    String username;

    final public String getPassword() {
        return Password;
    }

    final public String getUsername(){
        return username;
    }

    String Password;
    String email;

    public RegisterRequest(String password, String username){
        this.Password = password;
        this.username = username;
    }
}
