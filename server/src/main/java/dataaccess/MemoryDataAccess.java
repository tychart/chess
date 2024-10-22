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

    @Override
    public Map<String, UserData> getAllUsers() {
        return users;
    }

    @Override
    public void deleteAuthToken(String authToken) {
        for (UserData user : users.values()) {
            if (user.getAuthToken().equals(authToken)) {
                user.setAuthToken("");
                break;
            }
        }
        throw new IllegalArgumentException("Error: Provided authToken not found in the database, unauthorized");
    }

    @Override
    public void clearDatabase() {
        users = new HashMap<>();
    }
}
