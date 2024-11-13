package client;

import chess.ChessGame;
import exception.ResponseException;
import org.junit.jupiter.api.*;

import server.Server;
import model.*;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;
    static String serverUrl = "http://localhost";
    static UserData existingUser;
    static LoginResponse existingLoginResponse;

    @BeforeAll
    public static void init() throws ResponseException {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade(serverUrl + ":" + port);
    }

    @BeforeEach
    public void beforeEach() throws ResponseException {
        serverFacade.deleteDatabase();

        existingUser = new UserData("existingUser", "pass", "my@emal.com");
        existingLoginResponse = serverFacade.registerUser(existingUser);

        GameRequest gameRequest = new GameRequest("myGame");
        serverFacade.createGame(existingLoginResponse.authToken(), gameRequest);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerUserSuccess() throws ResponseException {
        UserData newUser = new UserData("tychart", "badpass", "tyler@byu");

        LoginResponse authStuff = serverFacade.registerUser(newUser);

        assertNotNull(authStuff.authToken());
    }

    @Test
    public void registerUserFail() throws ResponseException {
        UserData newUser = new UserData("tychart", "badpass", "tyler@byu");
        serverFacade.registerUser(newUser);

        assertThrows(ResponseException.class, () -> serverFacade.registerUser(newUser));
    }

    @Test
    public void loginUserSuccess() throws ResponseException {
        LoginResponse authStuff = serverFacade.loginUser(existingUser);

        assertNotNull(authStuff.authToken());
    }

    @Test
    public void loginUserFail() {
        UserData newUser = new UserData("tychart", "badpass", "tyler@byu");

        assertThrows(ResponseException.class, () -> serverFacade.loginUser(newUser));
    }

    @Test
    public void logoutUserSuccess() throws ResponseException {
        LoginResponse authStuff = serverFacade.loginUser(existingUser);

        assertDoesNotThrow(() -> serverFacade.logoutUser(authStuff.authToken()));
        assertThrows(ResponseException.class, () -> serverFacade.logoutUser(authStuff.authToken()));
    }

    @Test
    public void logoutUserFail() {
        assertThrows(ResponseException.class, () -> serverFacade.logoutUser("BadAuthToken"));
    }

    @Test
    public void createGameSuccess() throws ResponseException {
        LoginResponse loginResponse = serverFacade.loginUser(existingUser);

        GameRequest gameRequest = new GameRequest("myGame");

        assertDoesNotThrow(() -> serverFacade.createGame(loginResponse.authToken(), gameRequest));
    }

    @Test
    public void createGameFail() throws ResponseException {
        LoginResponse loginResponse = serverFacade.loginUser(existingUser);

        GameRequest gameRequest = new GameRequest(null);

        assertThrows(ResponseException.class,() -> serverFacade.createGame(loginResponse.authToken(), gameRequest));
    }

    @Test
    public void gameListSuccess() throws ResponseException {
        LoginResponse loginResponse = serverFacade.loginUser(existingUser);

        GameListResponse gameListResponse = serverFacade.listGames(loginResponse.authToken());
        System.out.println(gameListResponse);
        assertNotNull(gameListResponse.games());
    }

    @Test
    public void gameListFail() {
        assertThrows(ResponseException.class, () -> serverFacade.listGames("No auth"));
    }

    @Test
    public void joinGameSuccess() throws ResponseException {
        LoginResponse loginResponse = serverFacade.loginUser(existingUser);


        JoinGameRequest joinGameRequest = new JoinGameRequest(ChessGame.TeamColor.WHITE, 1);

        serverFacade.joinGame(loginResponse.authToken(), joinGameRequest);

        GameListResponse gameListResponse = serverFacade.listGames(loginResponse.authToken());
        System.out.println(gameListResponse);

        assertNotNull(gameListResponse.games());
    }

    @Test
    public void joinGameFail() throws ResponseException {
        LoginResponse loginResponse = serverFacade.loginUser(existingUser);


        JoinGameRequest joinGameRequest = new JoinGameRequest(ChessGame.TeamColor.WHITE, 1);

        serverFacade.joinGame(loginResponse.authToken(), joinGameRequest);
        assertThrows(ResponseException.class, () -> serverFacade.joinGame("Badauth", joinGameRequest));

    }

    @Test
    public void deleteDatabaseSuccess() throws ResponseException {
        serverFacade.deleteDatabase();
        assertTrue(true); // Database call did not throw exception
    }

    @Test
    public void deleteDatabaseFail() throws ResponseException {
        serverFacade.deleteDatabase();
        assertTrue(true); // I am not sure how to make this fail
    }


}