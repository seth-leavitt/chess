package results;

public class LogoutResult {
    boolean loggedOut;

    public LogoutResult(boolean loggedOut){
        this.loggedOut = loggedOut;
    }

    public boolean getStatus(){
        return loggedOut;
    }
}
