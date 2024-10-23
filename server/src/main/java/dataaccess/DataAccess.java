package dataaccess;

import com.google.protobuf.ServiceException;
import model.*;

import java.util.Map;

public interface DataAccess {

    // Add a new user to the database
    void addUser(UserData user);

    // Update a user's information given the username and the new information
    void updateUser(UserData user);

    // Get a user by their username
    UserData getUser(String username);

    Map<String, UserData> getAllUsers();

    void addAuthData(AuthData authData);

    UserData authenticateUser(String authToken) throws ServiceException;

    // Used for logging out a user
    void deleteAuthData(String authToken) throws ServiceException;

    int getNextGameID();

    void addGame(GameData gameData);

    GameData getGame(int gameID) throws ServiceException;

    Map<Integer, GameData> getAllGames();

    void clearDatabase();
}
