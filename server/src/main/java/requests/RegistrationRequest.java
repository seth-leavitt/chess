package requests;

public class RegistrationRequest {
    String username;
    String password;
    String email;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail(){
        return email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RegistrationRequest(String username, String password, String email){
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
