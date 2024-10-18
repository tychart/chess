package server;

import com.google.gson.Gson;
import com.google.protobuf.ServiceException;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import spark.*;

import service.Service;

public class Server {

    int port = 0;
    private final Gson gson = new Gson();
    private final DataAccess dataAccess = new MemoryDataAccess();
    private final Service service = new Service(dataAccess);

    public Server() {
    }

    public Server(int port) {
        this.port = port;
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        // Register your endpoints and handle exceptions here.
//        Spark.get("/test", (req, res) -> {
//            // Handle the request for the root path.
//            return "Hello, world!";
//        });

        Spark.delete("/db", (req, res) -> {
            return "{}";
        });

        Spark.post("/user", this::createUser);
        Spark.post("/session", this::loginUser);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private String createUser(Request req, Response res) {
        try {
            UserData newUser = gson.fromJson(req.body(), UserData.class);
            String returnJson = service.registerUser(newUser);
            System.out.println(returnJson);
            return returnJson;
        } catch (ServiceException e) {
            res.status(403);
            return gson.toJson(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            res.status(400);
            return gson.toJson(new ErrorResponse(e.getMessage()));
        }
    }

    private String loginUser(Request req, Response res) {
        try {
            UserData newLogin = gson.fromJson(req.body(), UserData.class);
            return service.loginUser(newLogin);
        } catch (ServiceException e) {
            res.status(403);
            return gson.toJson(new ErrorResponse(e.getMessage()));
        }
    }


    public int getPort() {
        return port;
    }
}
