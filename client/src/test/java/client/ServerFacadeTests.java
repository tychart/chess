import exception.ResponseException;
import org.junit.jupiter.api.*;



import server.Server;
import server.ServerFacade;
import model.*;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;
    static String serverUrl = "http://localhost:8080";

    @BeforeAll
    public static void init() throws ResponseException {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade(serverUrl);
        serverFacade.deleteDatabase();
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
}