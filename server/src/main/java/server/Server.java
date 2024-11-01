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
        return service.clearDatabase();
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
            UserData newLogin = gson.fromJson(req.body(), UserData.class);
            String jsonReturn = service.loginUser(newLogin);
            res.type("application/json");
//            System.out.println("jsonReturn: " + jsonReturn);
            return jsonReturn;
        } catch (ServiceException e) {
            res.status(401);
            return gson.toJson(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            System.out.println("IT GOT HERE, SOMETHING WENT VERY WRONG");
            res.status(400);
            return gson.toJson(new ErrorResponse(e.getMessage()));
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
//        return "";
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
