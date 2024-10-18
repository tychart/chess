package dataaccess;

import java.util.HashMap;
import java.util.Map;

import server.UserData;

public class MemoryDataAccess implements DataAccess {

    private Map<String, UserData> users;

    public MemoryDataAccess() {
        users = new HashMap<>();
    }

    @Override
    public void addUser(UserData user) {
        users.put(user.getUsername(), user);
    }

    @Override
    public void updateUser(UserData user) {
        users.replace(user.getUsername(), user);
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }
}
