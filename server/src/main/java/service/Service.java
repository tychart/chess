package service;

import com.google.protobuf.ServiceException;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

import com.google.gson.Gson;

import java.util.Map;

import dataaccess.DataAccess;
import model.*;
import chess.*;


public class Service {
    private final DataAccess dataAccess;
    private final Gson gson = new Gson();

    public Service(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public String clearDatabase() {
        dataAccess.clearDatabase();
        return "{}";
    }

    public String registerUser(UserData newUser) throws ServiceException {
        if (newUser.username() == null ||
                newUser.password() == null ||
                newUser.email() == null
        ) {
            throw new IllegalArgumentException("Error: Missing vital signup information, please fill out all fields");
        }

        if (dataAccess.getUser(newUser.username()) != null) {
            throw new ServiceException("Error: User already exists");
        }

        dataAccess.addUser(newUser);

        AuthData authData = new AuthData(newUser.username(), generateAuthToken());

        dataAccess.addAuthData(authData);

        LoginResponse loginResponse = new LoginResponse(newUser.username(), authData.authToken());

        // Return a JSON response with username and authToken
        return gson.toJson(loginResponse);
    }

    public String loginUser(UserData loginUser) throws ServiceException {
        if (
                dataAccess.getUser(loginUser.username()) == null ||
                        !dataAccess.getUser(loginUser.username()).password().equals(loginUser.password())
        ) {
            throw new ServiceException("Error: Invalid username or password");
        }

        AuthData authData = new AuthData(loginUser.username(), generateAuthToken());

        dataAccess.addAuthData(authData);

        LoginResponse loginResponse = new LoginResponse(loginUser.username(), authData.authToken());

        // Return a JSON response with username and authToken
        return gson.toJson(loginResponse);

    }

    public String logoutUser(String authToken) throws ServiceException {
        dataAccess.deleteAuthData(authToken);
        return "{}";
    }

    public Map<String, UserData> getAllUsers() {
        return dataAccess.getAllUsers();
    }

    public String createGame(String authToken, String gameName) throws ServiceException {
        if (gameName == null) {
            throw new ServiceException("Error: Please enter in a game name");
        }

        UserData currUser = dataAccess.authenticateUser(authToken);

        ChessGame game = new ChessGame();
        int gameID = dataAccess.getNextGameID();
        GameData gameData = new GameData(gameID, null, null, gameName, game);


        dataAccess.addGame(gameData);

        return gson.toJson(new GameIDResponse(gameID));
    }

    public String joinGame(String authToken, JoinGameRequest joinGameRequest) throws ServiceException {
        if (joinGameRequest == null ||
                joinGameRequest.playerColor() == null ||
                joinGameRequest.gameID() == null ||
                joinGameRequest.gameID() < 1
        ) {
            throw new ServiceException("Error: Join Game Request Malformed");
        }


        UserData currUser = dataAccess.authenticateUser(authToken);
        GameData updatedGame;
        int gameID = joinGameRequest.gameID();

        GameData gameData = dataAccess.getGame(gameID);
        if (Objects.equals(joinGameRequest.playerColor(), ChessGame.TeamColor.WHITE)) {
            if (gameData.whiteUsername() != null) {
                throw new ServiceException("Error: Forbidden White is already chosen. Is user: " + gameData.whiteUsername());
            }
            updatedGame = new GameData(
                    gameData.gameID(),
                    currUser.username(),
                    gameData.blackUsername(),
                    gameData.gameName(),
                    gameData.game()
            );
        } else {
            if (gameData.blackUsername() != null) {
                throw new ServiceException("Error: Forbidden White is already chosen. Is user: " + gameData.blackUsername());
            }
            updatedGame = new GameData(
                    gameData.gameID(),
                    gameData.whiteUsername(),
                    currUser.username(),
                    gameData.gameName(),
                    gameData.game()
            );
        }

        dataAccess.addGame(updatedGame);
        return "{}";
    }

    public String listGames(String authToken) throws ServiceException {
        dataAccess.authenticateUser(authToken);
        Map<Integer, GameData> gameMap = dataAccess.getAllGames();
        HashSet<GameDataSimple> gameSet = new HashSet<>();

        for (GameData game : gameMap.values()) {
            gameSet.add(new GameDataSimple(
                    game.gameID(),
                    game.whiteUsername(),
                    game.blackUsername(),
                    game.gameName()
            ));
        }
        return gson.toJson(new GameListResponse(gameSet));
    }


    private String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

}
