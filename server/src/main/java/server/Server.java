package server;

import com.google.gson.Gson;
import com.google.protobuf.ServiceException;
import dataaccess.SqlDataAccess;
import spark.*;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import service.Service;
import model.*;

public class Server {

    int port = 0;
    private final Gson gson = new Gson();
    private final DataAccess dataAccess = new SqlDataAccess();
    private final Service service = new Service(dataAccess);

    public Server() {}

    public Server(int port) {
        this.port = port;
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.delete("/db", this::clearDatabase);
        Spark.post("/user", this::createUser);
        Spark.post("/session", this::loginUser);
        Spark.delete("/session", this::logoutUser);
        Spark.post("/game", this::createGame);
        Spark.get("/game", this::listGames);
        Spark.put("/game", this::joinGame);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private String clearDatabase(Request req, Response res) {
        try {
            return service.clearDatabase();
        } catch (ServiceException e) {
            res.status(401);
            return gson.toJson(new ErrorResponse(e.getMessage()));
        }
    }

    private String createUser(Request req, Response res) {
        try {
            UserData newUser = gson.fromJson(req.body(), UserData.class);
            return service.registerUser(newUser);
        } catch (ServiceException e) {
            res.status(403);
            return gson.toJson(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            res.status(400);
            return gson.toJson(new ErrorResponse(e.getMessage()));
        }
    }

    private String loginUser(Request req, Response res) {
        try {
            // Try to parse the incoming JSON request body to UserData
            UserData newLogin = gson.fromJson(req.body(), UserData.class);
            String jsonReturn = service.loginUser(newLogin);

            res.type("application/json");
            return jsonReturn;
        } catch (ServiceException e) {
            // Handle the case of invalid username/password
            res.status(401);  // Unauthorized
            return gson.toJson(new ErrorResponse("Authentication failed: " + e.getMessage()));
        } catch (Exception e) {
            // Catch any other unexpected exceptions and log them
            e.printStackTrace();  // Log the full stack trace for debugging
            res.status(500);  // Internal server error
            return gson.toJson(new ErrorResponse("Internal server error: " + e.getMessage()));
        }
    }

    private String logoutUser(Request req, Response res) {
        try {
            // Extract the authToken from the request headers
            String authToken = req.headers("authorization");
            return service.logoutUser(authToken);
        } catch (ServiceException e) {
            res.status(401);
            return gson.toJson(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String createGame(Request req, Response res) {
        try {
            String authToken = req.headers("authorization");
            GameRequest gameRequest = gson.fromJson(req.body(), GameRequest.class);
            String gameName = gameRequest.gameName();

            return service.createGame(authToken, gameName);
        } catch (ServiceException e) {
            res.status(401);
            return gson.toJson(new ErrorResponse(e.getMessage()));
        }
    }

    private String listGames(Request req, Response res) {
        try {
            // Extract the authToken from the request headers
            String authToken = req.headers("authorization");
            return service.listGames(authToken);
        } catch (ServiceException e) {
            res.status(401);
            return gson.toJson(new ErrorResponse(e.getMessage()));
        }
    }

    private String joinGame(Request req, Response res) {
        try {
            // Extract the authToken from the request headers
            String authToken = req.headers("authorization");
            JoinGameRequest joinGameRequest = gson.fromJson(req.body(), JoinGameRequest.class);

            return service.joinGame(authToken, joinGameRequest);

        } catch (ServiceException e) {
            if (e.getMessage().contains("authorize")) {
                res.status(401);
            } else if (e.getMessage().contains("Forbidden")) {
                res.status(403);
            } else {
                res.status(400);
            }

            return gson.toJson(new ErrorResponse(e.getMessage()));
        }
    }


    public int getPort() {
        return port;
    }
}
