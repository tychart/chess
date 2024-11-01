package service;


import com.google.protobuf.ServiceException;
import dataaccess.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import service.Service;
import chess.*;


import static org.junit.jupiter.api.Assertions.*;

public class DataAccessTests {
    static DataAccess dataAccess = new SqlDataAccess();
//    static Service service = new Service(dataAccess);
    private final Gson gson = new Gson();

    @BeforeEach
    void clear() throws Exception {
        dataAccess.clearDatabase();
    }

    @Test
    void addUserSuccess() throws Exception {
        UserData newUser = new UserData("username", "badpass", "myemail");
        dataAccess.addUser(newUser);
        UserData retrievedUser = dataAccess.getUser(newUser.username());
//        System.out.println(jsonReturn);
//        Map<String, UserData> allUsers = service.getAllUsers();
//        assertEquals(1, allUsers.size());
        assertEquals(newUser, retrievedUser);
    }

    @Test
    void listAllUsersSuccess() throws Exception {
        UserData user1  = new UserData("username", "badpass", "myemail");
        UserData user2  = new UserData("username2", "badpass", "myemail");
        dataAccess.addUser(user1);
        dataAccess.addUser(user2);
        System.out.println(dataAccess.getAllUsers());
    }

    @Test
    void addAuthSuccess() throws Exception {
        UserData user1  = new UserData("username", "badpass", "myemail");
        AuthData newAuthData = new AuthData("username", "supernotcorrectauthtoken");
        dataAccess.addUser(user1);
        dataAccess.addAuthData(newAuthData);
        UserData retrievedUser = dataAccess.getUser(user1.username());
        UserData retrievedUserByToken = dataAccess.authenticateUser(newAuthData.authToken());
        assertEquals(retrievedUser, retrievedUserByToken);
    }

    @Test
    void removeAuthSuccess() throws Exception {
        UserData user1   = new UserData("username", "badpass", "myemail");
        AuthData newAuthData  = new AuthData("username", "supernotcorrectauthtoken");
        dataAccess.addUser(user1);
        dataAccess.addAuthData(newAuthData);
        UserData retrievedUserBeforeDeletingToken = dataAccess.getUser(user1.username());
        dataAccess.deleteAuthData(newAuthData.authToken());

        assertNotNull(retrievedUserBeforeDeletingToken);
        assertThrows(ServiceException.class, () -> dataAccess.authenticateUser(newAuthData.authToken()));

    }

    @Test
    void addGameSuccess() throws Exception {
        UserData user1   = new UserData("username", "badpass", "myemail");
        GameData newGameData = new GameData(1, "wuser", "buser", "game1", new ChessGame());
        dataAccess.addUser(user1);
        dataAccess.addGame(newGameData);
        GameData retrievedGameData = dataAccess.getGame(1);
        assertNotNull(retrievedGameData);
    }

}