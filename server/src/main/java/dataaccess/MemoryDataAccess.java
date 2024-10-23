package dataaccess;

import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.ServiceException;
import model.*;

public class MemoryDataAccess implements DataAccess {

    private Map<String, UserData> users;
    private Map<String, AuthData> authDataTable;
    private Map<String, AuthData> gameDataTable;

    public MemoryDataAccess() {
        users = new HashMap<>();
        authDataTable = new HashMap<>();
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
        this.authDataTable.put(authData.username(), authData);
    }

    @Override
    public Map<String, UserData> getAllUsers() {
        return users;
    }

    @Override
    public void deleteAuthData(String authToken) throws ServiceException {
//        for (AuthData authData : this.authDataTable.values()) {
//            if (authData.authToken().equals(authToken)) {
//                this.authDataTable.remove(authData.username());
//                return;
//            }
//        }
//
//
////        for (UserData user : users.values()) {
////            if (user.getAuthToken().equals(authToken)) {
////                user.setAuthToken("");
////                return;
////            }
////        }
//        throw new ServiceException("Error: Provided authToken not found in the database, unauthorized");

        String userToLogOut = this.authenticateUser(authToken).username();
        this.authDataTable.remove(userToLogOut);
    }

    @Override
    public UserData authenticateUser(String authToken) throws ServiceException {
        for (AuthData authData : this.authDataTable.values()) {
            if (authData.authToken().equals(authToken)) {
                return this.getUser(authData.username());
            }
        }
        throw new ServiceException("Error: Provided authToken not found in the database, unauthorized");
    }

    @Override
    public GameData addGame(GameData gameData) {

    }

    @Override
    public void clearDatabase() {
        users.clear();
        authDataTable.clear();
    }
}
