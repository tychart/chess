package client;

import java.util.Arrays;


import com.google.gson.Gson;

import exception.ResponseException;
import model.*;
import server.ServerFacade;
import chess.ChessGame;


public class ChessClient {
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;
    private String authToken = null;

    public ChessClient(String serverUrl) {
        this.serverUrl = serverUrl;
        this.server = new ServerFacade(this.serverUrl);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (state) {
                case SIGNEDOUT -> unauthenicatedSwitch(params, cmd);
                case SIGNEDIN -> authenicatedSwitch(params, cmd);
                case GAMEPLAY -> gameplaySwitch(params, cmd);
            };

        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String unauthenicatedSwitch(String[] params, String cmd) throws ResponseException {
        return switch (cmd) {
            case "login", "-l" -> login(params);
            case "register", "-r" -> register(params);
            case "quit", "-q" -> "quit";
            default -> help();
        };
    }

    private String authenicatedSwitch(String[] params, String cmd) throws ResponseException {
        return switch (cmd) {
            case "join", "-j" -> joinGame(params);
            case "list-games", "-l" -> listGames(params);
            case "create", "-c" -> createGame(params);
            case "logout", "-r" -> logout(params);
            case "quit", "-q" -> "quit";
            default -> help();
        };
    }

    private String gameplaySwitch(String[] params, String cmd) throws ResponseException {
        return switch (cmd) {
            case "login", "-l" -> login(params);
            case "register", "-r" -> register(params);
//                case "list" -> listPets();
//                case "signout" -> signOut();
//                case "adopt" -> adoptPet(params);
//                case "adoptall" -> adoptAllPets();
            case "quit", "-q" -> "quit";
            default -> help();
        };
    }

    public String login(String[] params) throws ResponseException {
        if (params.length == 2) {

            // Extract individual parameters by index
            String username = params[0];
            String password = params[1];

            UserData loginUser = new UserData(username, password, null);
            LoginResponse loginResponse = server.loginUser(loginUser);

            this.authToken = loginResponse.authToken();

            state = State.SIGNEDIN;


            return String.format("Welcome %s! You are signed in, now you can join a game!", username);
        }
        throw new ResponseException(400, "Expected: <username> <password>");
    }

    public String register(String[] params) throws ResponseException {
        if (params.length == 3) {

            // Extract individual parameters by index
            String username = params[0];
            String password = params[1];
            String email = params[2];

            // Create a new UserData object with these parameters
            UserData newUser = new UserData(username, password, email);
            LoginResponse loginResponse = server.registerUser(newUser);

            this.authToken = loginResponse.authToken();

            state = State.SIGNEDIN;
            return String.format("Welcome %s! You are signed in, now you can join a game!", username);
        }
        throw new ResponseException(400, "Expected: <username> <password> <email>");
    }

    public String logout(String[] params) throws ResponseException {
        server.logoutUser(this.authToken);
        authToken = null;
        state = State.SIGNEDOUT;
        return "Successfully logged out, returning to the unauthenticated state";
    }

    public String joinGame(String[] params) throws ResponseException {


        // If is not an observer, then it must be a player
        if (params.length == 2 && !params[0].toUpperCase().equals("OBSERVER")) {
            ChessGame.TeamColor newTeamColor = parseTeamColor(params[1]);
            int gameID = Integer.parseInt(params[0]);
            JoinGameRequest joinGameRequest = new JoinGameRequest(newTeamColor, gameID);
            server.joinGame(authToken, joinGameRequest);
            return "Successfully joined the game";
        } else {
            // Is observer
            int gameID = Integer.parseInt(params[0]);
        }


        return "";
    }

    public String listGames(String[] params) throws ResponseException {
        GameListResponse gameListResponse = server.listGames(authToken);
        return gameListResponse.games().toString();

//        return "";
    }

    public String createGame(String[] params) throws ResponseException {
        if (params.length >= 1) {
            GameRequest gameRequest = new GameRequest(params[0]);
            server.createGame(authToken, gameRequest);
            return "Game " + params[0] + " successfully created";
        }

        throw new ResponseException(400, "Expected: <NAME>");
    }

    private ChessGame.TeamColor parseTeamColor(String color) {
        return ChessGame.TeamColor.valueOf(color.toUpperCase());
    }



    public String help() {

        return switch (state) {
            case SIGNEDOUT -> """
                    - login <username> <password>
                    - register <username> <password> <email>
                    - help
                    - quit
                    """;
            case SIGNEDIN -> """
                    - join <Game Number> [White|Black|Observer] (Default=Observer)
                    - list-games
                    - create <NAME>
                    - logout
                    - help
                    - quit
                    """;
            case GAMEPLAY -> """
                    - {To Impliment in Phase 6}
                    - register <username> <password> <email>
                    - help
                    - quit
                    """;
        };
    }


}
