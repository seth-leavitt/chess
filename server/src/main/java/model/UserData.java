package model;


public class UserData {
    public String getPassword() {
        return password;
    }

    String username;
    String password;

    public UserData(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername(){
        return username;
    }
}
