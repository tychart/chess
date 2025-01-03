package dataaccess;

import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.ServiceException;

import model.*;

public class MemoryDataAccess implements DataAccess {

    private Map<String, UserData> users;
    private Map<String, AuthData> authDataTable;
    private Map<Integer, GameData> gameDataTable;

    public MemoryDataAccess() {
        users = new HashMap<>();
        authDataTable = new HashMap<>();
        gameDataTable = new HashMap<>();
    }

    @Override
    public void addUser(UserData user) {
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public void addAuthData(AuthData authData) throws ServiceException {
        this.authDataTable.put(authData.authToken(), authData);
    }

    @Override
    public Map<String, UserData> getAllUsers() {
        return users;
    }

    @Override
    public void deleteAuthData(String authToken) throws ServiceException {

//        String userToLogOut = this.authenticateUser(authToken);
        this.authDataTable.remove(authToken);
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
    public int getNextGameID() {
        int newID = 1;
        while (gameDataTable.containsKey(newID)) {
            newID++;
        }
        return newID;
    }

    @Override
    public void addGame(GameData gameData) {
        this.gameDataTable.put(gameData.gameID(), gameData);
    }

    @Override
    public void deleteGame(int gameID) throws ServiceException {
        this.gameDataTable.remove(gameID);
    }

    @Override
    public GameData getGame(int gameID) throws ServiceException {
        GameData gameData = this.gameDataTable.get(gameID);
        if (gameData == null) {
            throw new ServiceException("Error: Game with ID " + gameID + " not found");
        }
        return gameData;
    }

    @Override
    public Map<Integer, GameData> getAllGames() {
        return gameDataTable;
    }

    @Override
    public void clearDatabase() {
        users.clear();
        authDataTable.clear();
        gameDataTable.clear();
    }
}
