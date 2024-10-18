package server;

import spark.*;

public class Server {

    int port = 0;

    public Server() {
    }

    public Server(int port) {
        this.port = port;
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        // Register your endpoints and handle exceptions here.
        Spark.get("/test", (req, res) -> {
            // Handle the request for the root path.
            return "Hello, world!";
        });

        Spark.delete("/db", (req, res) -> {
            return "{}";
        });

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public int getPort() {
        return port;
    }
}
