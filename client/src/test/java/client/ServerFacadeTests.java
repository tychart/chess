import exception.ResponseException;
import org.junit.jupiter.api.*;



import server.Server;
import server.ServerFacade;
import model.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;
    static String serverUrl = "http://localhost:8080";
    static UserData existingUser;

    @BeforeAll
    public static void init() throws ResponseException {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade(serverUrl);
        serverFacade.deleteDatabase();
        existingUser = new UserData("existingUser", "pass", "my@emal.com");
        serverFacade.registerUser(existingUser);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerUserSuccess() throws ResponseException {
        UserData newUser = new UserData("tychart", "badpass", "tyler@byu");

        LoginResponse authStuff = serverFacade.registerUser(newUser);

        Assertions.assertNotNull(authStuff.authToken());
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

        Assertions.assertNotNull(authStuff.authToken());
    }

    @Test
    public void loginUserFail() throws ResponseException {
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
    public void logoutUserFail() throws ResponseException {
        UserData newUser = new UserData("tychart", "badpass", "tyler@byu");

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
}