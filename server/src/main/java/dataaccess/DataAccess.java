package dataaccess;

import server.UserData;

public interface DataAccess {

    // Add a new user to the database
    void addUser(UserData user);

    // Update a user's information given the username and the new information
    void updateUser(UserData user);

    // Get a user by their username
    UserData getUser(String username);
}
