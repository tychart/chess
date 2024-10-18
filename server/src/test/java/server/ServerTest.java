//package server;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import java.net.http.HttpRequest;
//
//import com.google.gson.Gson;
//
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//
//import server.Server;
//
//public class ServerTest {
//
//    private static Server server;
//    private final Gson gson = new Gson();
//
//    @BeforeAll
//    public static void setup() {
//        server = new Server();
//        server.run(4567);
//    }
//
//    @AfterAll
//    public static void tearDown() {
//        server.stop();
//    }
//
//
//    @Test
//    public void testPort() {
//        assertEquals(4567, server.getPort());
//    }
//
//    @Test
//    public void createUser_ValidUser_ReturnsSuccessResponse() {
//        UserData newUser = new UserData("username", "password", "email");
//        String requestBody = gson.toJson(newUser);
//
//        String responseBody = server.createUser(new Request(requestBody), new Response());
//
//        assertEquals("{\"success\":true}", responseBody);
//    }
//
//    @Test
//    public void createUser_DuplicateUsername_ReturnsErrorResponse() {
//        UserData newUser = new UserData("username", "password", "email");
//        String requestBody = gson.toJson(newUser);
//
//        server.createUser(new Request(requestBody), new Response());
//        String responseBody = server.createUser(new Request(requestBody), new Response());
//
//        assertEquals("{\"error\":\"Username is already in use\"}", responseBody);
//    }
//}
//
