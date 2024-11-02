package dataaccess;

import com.google.protobuf.ServiceException;
import model.*;

import java.util.Map;

public interface DataAccess {

    // Add a new user to the database
    void addUser(UserData user) throws ServiceException;


    // Get a user by their username
    UserData getUser(String username) throws ServiceException;

    Map<String, UserData> getAllUsers() throws ServiceException;

    void addAuthData(AuthData authData) throws ServiceException;

    UserData authenticateUser(String authToken) throws ServiceException;

    // Used for logging out a user
    void deleteAuthData(String authToken) throws ServiceException;

    int getNextGameID() throws ServiceException;

    void addGame(GameData gameData) throws ServiceException;

    void deleteGame(int gameID) throws ServiceException;

    GameData getGame(int gameID) throws ServiceException;

    Map<Integer, GameData> getAllGames() throws ServiceException;

    void clearDatabase();
}
