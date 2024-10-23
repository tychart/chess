package service;


import com.google.protobuf.ServiceException;
import dataaccess.MemoryDataAccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.*;
import service.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTest {

    static final Service service = new Service(new MemoryDataAccess());
    private final Gson gson = new Gson();

    @BeforeEach
    void clear() throws Exception {
        service.clearDatabase();
    }

    @Test
    void registerUser() throws Exception {
        UserData newUser = new UserData("username", "badpass", "myemail");
        String jsonReturn = service.registerUser(newUser);
        System.out.println(jsonReturn);
        Map<String, UserData> allUsers = service.getAllUsers();
        assertEquals(1, allUsers.size());
        assertTrue(allUsers.containsKey(newUser.username()));
    }

    @Test
    void registerUserFail() throws Exception {
        UserData newUser = new UserData("username", null, "myemail");
        assertThrows(IllegalArgumentException.class, () -> service.registerUser(newUser));
//        System.out.println(jsonReturn);
        Map<String, UserData> allUsers = service.getAllUsers();
        assertEquals(0, allUsers.size());
        assertFalse(allUsers.containsKey(newUser.username()));
    }

    @Test
    void registerUserFailDuplicate() throws Exception {
        UserData newUser = new UserData("username", "badpass", "myemail");
        service.registerUser(newUser);
        assertThrows(ServiceException.class, () -> service.registerUser(newUser));
        Map<String, UserData> allUsers = service.getAllUsers();
        assertEquals(1, allUsers.size());
        assertTrue(allUsers.containsKey(newUser.username()));
    }

    @Test
    void loginUserSuccess() throws Exception {
        UserData user = new UserData("username", "password", "email");
        service.registerUser(user);

        UserData loginUser = new UserData("username", "password", null);
        String jsonReturn = service.loginUser(loginUser);

        // Assert that the auth token is present in the JSON response
        Map<String, Object> response = gson.fromJson(jsonReturn, HashMap.class);
        assertNotNull(response.get("authToken"));
    }

    @Test
    void loginUserInvalidUsername() throws Exception {
        UserData user = new UserData("username", "password", "email");
        service.registerUser(user);

        UserData loginUser = new UserData("invalid_username", "password", null);
        assertThrows(ServiceException.class, () -> service.loginUser(loginUser));
    }

    @Test
    void loginUserInvalidPassword() throws Exception {
        UserData user = new UserData("username", "password", "email");
        service.registerUser(user);

        UserData loginUser = new UserData("username", "invalid_password", null);
        assertThrows(ServiceException.class, () -> service.loginUser(loginUser));
    }

    @Test
    void logoutUserSuccess() throws Exception {
        UserData user = new UserData("username", "password", "email");
        service.registerUser(user);
        String loggedInUserJson = service.loginUser(user);
        LoginResponse logedInUser = gson.fromJson(loggedInUserJson, LoginResponse.class);


        String authToken = logedInUser.authToken();
        String jsonReturn = service.logoutUser(authToken);

        assertEquals("{}", jsonReturn);
    }

    @Test
    void logoutUserInvalidAuthToken() throws Exception {
        assertThrows(ServiceException.class, () -> service.logoutUser("invalid_auth_token"));
    }

    @Test
    void createGame_Success() throws Exception {
        UserData user = new UserData("username", "password", "email");
        service.registerUser(user);
        String loggedInUserJson = service.loginUser(user);
        LoginResponse logedInUser = gson.fromJson(loggedInUserJson, LoginResponse.class);

        String authToken = logedInUser.authToken();
        String gameName = "testGame";

        String jsonReturn = service.createGame(authToken, gameName);
        String jsonGameList = service.listGames(authToken);

        GameListResponse allGames = gson.fromJson(jsonGameList, GameListResponse.class);
        assertEquals(1, allGames.gameDataSimpleSet().size());
    }

    @Test
    void createGame_Unauthorized() throws Exception {
        String gameName = "testGame";

        // Test unauthorized creation
        assertThrows(ServiceException.class, () -> service.createGame("invalidAuthToken", gameName));

        // Test unauthorized listing
        assertThrows(ServiceException.class, () -> {
            String jsonGameList = service.listGames("randWrongAuthToken");
        });
    }

    @Test
    void createGame_MissingGameName() throws Exception {
        UserData user = new UserData("username", "password", "email");
        service.registerUser(user);
        String loggedInUserJson = service.loginUser(user);
        LoginResponse logedInUser = gson.fromJson(loggedInUserJson, LoginResponse.class);

        String authToken = logedInUser.authToken();

        assertThrows(ServiceException.class, () -> service.createGame(authToken, null));
        String jsonGameList = service.listGames(authToken);
        GameListResponse allGames = gson.fromJson(jsonGameList, GameListResponse.class);
        assertEquals(0, allGames.gameDataSimpleSet().size());
    }


}
