package dataaccess;


import com.google.protobuf.ServiceException;
import dataaccess.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

import dataaccess.DataAccess;
import model.*;
import chess.*;


import static org.junit.jupiter.api.Assertions.*;

public class DataAccessTests {
    static DataAccess dataAccess = new SqlDataAccess();
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
        assertEquals(newUser, retrievedUser);
    }

    @Test
    void addUserFail() throws Exception {
        UserData newUser = new UserData("username", null, "myemail");
        assertThrows(ServiceException.class, () -> dataAccess.addUser(newUser));
    }

    @Test
    void getUserSuccess() throws Exception {
        UserData newUser = new UserData("username", "badpass", "myemail");
        dataAccess.addUser(newUser);
        UserData retrievedUser = dataAccess.getUser(newUser.username());
        assertEquals(newUser, retrievedUser);
    }

    @Test
    void getUserFail() throws Exception {
        UserData newUser = new UserData("username", "badpass", "myemail");
        dataAccess.addUser(newUser);
        UserData returnedUser = dataAccess.getUser("Incorrect Username");
        assertNull(returnedUser);
    }

    @Test
    void getAllUsersSuccess() throws Exception {
        UserData user1  = new UserData("username", "badpass", "myemail");
        UserData user2  = new UserData("username2", "badpass", "myemail");
        dataAccess.addUser(user1);
        dataAccess.addUser(user2);
        System.out.println(dataAccess.getAllUsers());
    }

    @Test
    void getAllUsersFail() throws Exception {
        UserData user1  = new UserData("username", "badpass", "myemail");
        UserData user2  = new UserData("username2", "badpass", "myemail");
        UserData user3  = new UserData(null, "badpass", "myemail");
        dataAccess.addUser(user1);
        dataAccess.addUser(user2);
        System.out.println(dataAccess.getAllUsers());
        assertThrows(ServiceException.class, () -> dataAccess.addUser(user3));
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
    void addAuthFail() throws Exception {
        UserData user1  = new UserData("username", "badpass", "myemail");
        AuthData newAuthData = new AuthData(null, "supernotcorrectauthtoken");
        dataAccess.addUser(user1);
        assertThrows(ServiceException.class, () -> dataAccess.addAuthData(newAuthData));
    }

    @Test
    void authenticateUserSuccess() throws Exception {
        UserData user1  = new UserData("username", "badpass", "myemail");
        AuthData newAuthData = new AuthData("username", "supernotcorrectauthtoken");
        dataAccess.addUser(user1);
        dataAccess.addAuthData(newAuthData);
        UserData retrievedUser = dataAccess.getUser(user1.username());
        UserData retrievedUserByToken = dataAccess.authenticateUser(newAuthData.authToken());
        assertEquals(retrievedUser, retrievedUserByToken);
    }

    @Test
    void authenticateUserFail() throws Exception {
        UserData user1  = new UserData("username", "badpass", "myemail");
        AuthData newAuthData = new AuthData("username", "currentauthtoken");
        dataAccess.addUser(user1);
        dataAccess.addAuthData(newAuthData);
        UserData retrievedUser = dataAccess.getUser(user1.username());
        assertThrows(ServiceException.class, () -> dataAccess.authenticateUser("incorrectauthtoken"));
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
    void removeAuthFail() throws Exception {
        UserData user1   = new UserData("username", "badpass", "myemail");
        AuthData newAuthData  = new AuthData("username", "supernotcorrectauthtoken");
        dataAccess.addUser(user1);
        dataAccess.addAuthData(newAuthData);
        UserData retrievedUserBeforeDeletingToken = dataAccess.getUser(user1.username());

        assertThrows(ServiceException.class, () -> dataAccess.deleteAuthData("wrongauthtoken"));
    }

    @Test
    void getNextGameIDSuccess() throws Exception {
        UserData user1   = new UserData("username", "badpass", "myemail");
        GameData newGameData = new GameData(1, "wuser", "buser", "game1", new ChessGame());
        dataAccess.addUser(user1);
        dataAccess.addGame(newGameData);
        GameData retrievedGameData = dataAccess.getGame(1);

        System.out.println(retrievedGameData);

        assertEquals(retrievedGameData.gameID(), newGameData.gameID());
        assertEquals(retrievedGameData.whiteUsername(), newGameData.whiteUsername());
        assertEquals(retrievedGameData.blackUsername(), newGameData.blackUsername());
        assertEquals(retrievedGameData.gameName(), newGameData.gameName());

        assertEquals(dataAccess.getNextGameID(), 2);
    }

    @Test
    void getNextGameIDFail() throws Exception {
        UserData user1   = new UserData("username", "badpass", "myemail");
        GameData newGameData = new GameData(1, "wuser", "buser", "game1", new ChessGame());
        dataAccess.addUser(user1);
        dataAccess.addGame(newGameData);
        GameData retrievedGameData = dataAccess.getGame(1);

        System.out.println(retrievedGameData);

        assertEquals(retrievedGameData.gameID(), newGameData.gameID());
        assertEquals(retrievedGameData.whiteUsername(), newGameData.whiteUsername());
        assertEquals(retrievedGameData.blackUsername(), newGameData.blackUsername());
        assertEquals(retrievedGameData.gameName(), newGameData.gameName());

        assertNotEquals(dataAccess.getNextGameID(), 3);
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

    @Test
    void addGameFail() throws Exception {
        UserData user1   = new UserData("username", "badpass", "myemail");
        GameData newGameData = new GameData(1, null, null, null, new ChessGame());
        dataAccess.addUser(user1);
        assertThrows(ServiceException.class, () -> dataAccess.addGame(newGameData));
    }

    @Test
    void deleteGameSuccess() throws Exception {
        UserData user1   = new UserData("username", "badpass", "myemail");
        GameData newGameData = new GameData(1, "wuser", "buser", "game1", new ChessGame());
        dataAccess.addUser(user1);
        dataAccess.addGame(newGameData);
        GameData retrievedGameData = dataAccess.getGame(1);
        dataAccess.deleteGame(1);
        assertNotNull(retrievedGameData);
    }

    @Test
    void deleteGameFail() throws Exception {
        UserData user1   = new UserData("username", "badpass", "myemail");
        GameData newGameData = new GameData(1, "wuser", "buser", "game1", new ChessGame());
        dataAccess.addUser(user1);
        dataAccess.addGame(newGameData);
        GameData retrievedGameData = dataAccess.getGame(1);
        dataAccess.deleteGame(1);
        assertThrows(ServiceException.class, () -> dataAccess.getGame(1));
    }

    @Test
    void getGameSuccess() throws Exception {
        UserData user1   = new UserData("username", "badpass", "myemail");
        GameData newGameData = new GameData(1, "wuser", "buser", "game1", new ChessGame());
        dataAccess.addUser(user1);
        dataAccess.addGame(newGameData);
        GameData retrievedGameData = dataAccess.getGame(1);
        assertNotNull(retrievedGameData);
    }

    @Test
    void getGameFail() throws Exception {
        UserData user1   = new UserData("username", "badpass", "myemail");
        GameData newGameData = new GameData(1, "wuser", "buser", "game1", new ChessGame());
        dataAccess.addUser(user1);
        dataAccess.addGame(newGameData);
        assertThrows(ServiceException.class, () -> dataAccess.getGame(2));
    }

    @Test
    void getAllGamesSuccess() throws Exception {
        UserData user1   = new UserData("username", "badpass", "myemail");
        GameData newGameData1 = new GameData(1, "wuser", "buser", "game1", new ChessGame());
        GameData newGameData2 = new GameData(2, "wuser", "buser", "game2", new ChessGame());
        dataAccess.addUser(user1);
        dataAccess.addGame(newGameData1);
        dataAccess.addGame(newGameData2);
        var retrievedGameData = dataAccess.getAllGames();

        System.out.println(retrievedGameData);

        assertNotNull(retrievedGameData);
    }

    @Test
    void getAllGamesFail() throws Exception {
        UserData user1   = new UserData("username", "badpass", "myemail");
        GameData newGameData1 = new GameData(1, "wuser", "buser", "game1", new ChessGame());
        GameData newGameData2 = new GameData(2, "wuser", "buser", null, new ChessGame());
        dataAccess.addUser(user1);
        dataAccess.addGame(newGameData1);

        assertThrows(ServiceException.class, () -> dataAccess.addGame(newGameData2));

        var retrievedGameData = dataAccess.getAllGames();
        System.out.println(retrievedGameData);
        assertNotNull(retrievedGameData);
    }

    @Test
    void clearDatabaseSuccess() throws Exception {
        UserData user1    = new UserData("username", "badpass", "myemail");
        GameData newGameData1  = new GameData(1, "wuser", "buser", "null", new ChessGame());
        dataAccess.addUser(user1);
        dataAccess.addGame(newGameData1);

        dataAccess.clearDatabase();

        assertNull(dataAccess.getUser(user1.username()));
    }

}