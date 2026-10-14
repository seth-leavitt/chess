package dataaccess;

import java.util.HashMap;
import model.UserData;

public class MemoryDataAccess extends DataAccess {
    HashMap<String, UserData> users = new HashMap<>();

    public void register(UserData userData){
        users.put(userData.username, userData);
    }

    public UserData getUser(String username){
        return users.get(username);
    }
}
