package server;

import static org.junit.jupiter.api.Assertions.*;

import java.net.http.HttpRequest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ServerTest {

    private static Server server;

    @BeforeAll
    public static void setup() {
        server = new Server();
        server.run(4567);
    }

    @AfterAll
    public static void tearDown() {
        server.stop();
    }

    @Test
    public void testRootEndpoint() {
        String response = HttpRequest.get("localhost", 4567, "/test").asString();
        assertEquals("Hello, world!", response);
    }

    @Test
    public void testInvalidEndpoint() {
        String response = HttpRequest.get("localhost", 4567, "/invalid").asString();
        assertEquals("404 Not Found", response);
    }

    @Test
    public void testPort() {
        assertEquals(4567, server.getPort());
    }
}

