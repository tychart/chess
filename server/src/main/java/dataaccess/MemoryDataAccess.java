package dataaccess;

import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.ServiceException;
import model.*;

public class MemoryDataAccess implements DataAccess {

    private Map<String, UserData> users;
    private Map<String, AuthData> authData;

    public MemoryDataAccess() {
        users = new HashMap<>();
        authData = new HashMap<>();
    }

    @Override
    public void addUser(UserData user) {
        users.put(user.username(), user);
    }

    @Override
    public void updateUser(UserData user) {
        users.replace(user.username(), user);
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public void addAuthData(AuthData authData) {
        this.authData.put(authData.username(), authData);
    }

    @Override
    public Map<String, UserData> getAllUsers() {
        return users;
    }

    @Override
    public void deleteAuthToken(String authToken) throws ServiceException {
        for (UserData user : users.values()) {
            if (user.getAuthToken().equals(authToken)) {
                user.setAuthToken("");
                return;
            }
        }
        throw new ServiceException("Error: Provided authToken not found in the database, unauthorized");
    }

    @Override
    public void clearDatabase() {
        users = new HashMap<>();
    }
}
