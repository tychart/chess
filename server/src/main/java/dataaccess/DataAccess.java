package dataaccess;

import server.UserData;

import java.util.Map;

public interface DataAccess {

    // Add a new user to the database
    void addUser(UserData user);

    // Update a user's information given the username and the new information
    void updateUser(UserData user);

    // Get a user by their username
    UserData getUser(String username);

    Map<String, UserData> getAllUsers();

    // Used for loging out a user
    void deleteAuthToken(String authToken);

    void clearDatabase();
}
