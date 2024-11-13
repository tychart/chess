package client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

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

    public String eval(String input) throws ResponseException {

        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);

        return switch (state) {
            case SIGNEDOUT -> unauthenicatedSwitch(params, cmd);
            case SIGNEDIN -> authenicatedSwitch(params, cmd);
            case GAMEPLAY -> gameplaySwitch(params, cmd);
            case OBSERVER -> "";
        };
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
            case "logout", "-q" -> logout(params);
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
        if (params.length != 2) {
            throw new ResponseException(400, "Invalid input! Expected: <username> <password>");
        }

        // Extract individual parameters
        String username = params[0];
        String password = params[1];
        UserData loginUser = new UserData(username, password, null);

        try {
            LoginResponse loginResponse = server.loginUser(loginUser);
            this.authToken = loginResponse.authToken();
            state = State.SIGNEDIN;
            return String.format("Welcome %s! You are signed in, now you can join a game!", username);
        } catch (ResponseException e) {
            throw new ResponseException(e.StatusCode(), "Login failed.  " + e.getMessage());
        }
    }


    public String register(String[] params) throws ResponseException {
        if (params.length != 3) {
            throw new ResponseException(400, "Invalid Input! Expected: <username> <password> <email>");
        }

        // Extract individual parameters by index
        String username = params[0];
        String password = params[1];
        String email = params[2];

        // Create a new UserData object with these parameters
        UserData newUser = new UserData(username, password, email);

        try {
            LoginResponse loginResponse = server.registerUser(newUser);
            this.authToken = loginResponse.authToken();
            state = State.SIGNEDIN;
            return String.format("Welcome %s! You are signed in, now you can join a game!", username);
        } catch (ResponseException e) {
            throw new ResponseException(e.StatusCode(), "Registration failed: " + e.getMessage());
        }
    }


    public String logout(String[] params) throws ResponseException {
        try {
            server.logoutUser(this.authToken);
            authToken = null;
            state = State.SIGNEDOUT;
            return "Successfully logged out, returning to the unauthenticated state";
        } catch (ResponseException e) {
            throw new ResponseException(401, "Invalid Auth Token");
        }
    }

    public String joinGame(String[] params) throws ResponseException {
        // Validate game index is provided
        if (params.length == 0) {
            throw new ResponseException(400, "Error: Game index is required.");
        }

        int gameIndex;
        String gameName;
        int gameID;
        try {
            gameIndex = Integer.parseInt(params[0]) - 1;
            List<GameDataSimple> sortedGames = getSortedGameList();
            gameID = sortedGames.get(gameIndex).gameID();
            gameName = sortedGames.get(gameIndex).gameName();
        } catch (NumberFormatException e) {
            throw new ResponseException(400, "Error: Invalid Game index format: " + params[0]);
        } catch (IndexOutOfBoundsException e) {
            throw new ResponseException(400, "Error: Game index is out of range.");
        }

        // Handle observer case
        if (params.length == 1 || "OBSERVER".equalsIgnoreCase(params[1])) {
            JoinGameRequest joinGameRequest = new JoinGameRequest(null, gameID); // null for observer
//            server.joinGame(authToken, joinGameRequest);
            state = State.OBSERVER;
            return "Successfully joined game " + gameName + " as an observer.";
        }

        // Handle player case
        ChessGame.TeamColor teamColor;
        try {
            teamColor = parseTeamColor(params[1]);
            JoinGameRequest joinGameRequest = new JoinGameRequest(teamColor, gameID);
            server.joinGame(authToken, joinGameRequest);
            state = State.GAMEPLAY;
            return "Successfully joined game " + gameName + " as " + teamColor;

        } catch (IllegalArgumentException e) {
            throw new ResponseException(400, "Error: Invalid team color. Use 'WHITE', 'BLACK', or 'OBSERVER'.");
        }

    }

    public String listGames(String[] params) {
        List<GameDataSimple> sortedGames;
        try {
            sortedGames = getSortedGameList();
        } catch (ResponseException e) {
            return "Error: Failed to list games.";
        }

        return prettyToStringGameListResponse(sortedGames);
    }

    private List<GameDataSimple> getSortedGameList() throws ResponseException {
        GameListResponse gameListResponse = server.listGames(authToken);

        // Sort games by gameID
        List<GameDataSimple> sortedGames = new ArrayList<>(gameListResponse.games());
        sortedGames.sort(Comparator.comparingInt(GameDataSimple::gameID));
        return sortedGames;
    }

    public String createGame(String[] params) throws ResponseException {
        if (params.length < 1) {
            throw new ResponseException(400, "Invalid input! Expected: <NAME>");
        }

        GameRequest gameRequest = new GameRequest(params[0]);

        try {
            server.createGame(authToken, gameRequest);
            return "Game '" + params[0] + "' successfully created!";
        } catch (ResponseException e) {
            throw new ResponseException(e.StatusCode(), "Failed to create game: " + e.getMessage());
        }
    }

    private ChessGame.TeamColor parseTeamColor(String color) {
        return ChessGame.TeamColor.valueOf(color.toUpperCase());
    }

    private String prettyToStringGameListResponse(List<GameDataSimple> sortedGames){
        StringBuilder outStr = new StringBuilder();
        int idWidth = 8;        // Width for Game ID
        int nameWidth = 30;     // Width for Game Name
        int userWidth = 20;     // Width for Username columns
        int counter = 1;


        // Header with column names
        outStr.append("----------------------------------------------------------------------------------\n");
        outStr.append(String.format("%-" + idWidth + "s", "Game #"));
        outStr.append(String.format("%-" + nameWidth + "s", "Game Name"));
        outStr.append(String.format("%-" + userWidth + "s", "White Player"));
        outStr.append(String.format("%-" + userWidth + "s", "Black Player"));
        outStr.append("\n");
        outStr.append("----------------------------------------------------------------------------------\n");

        // Game data rows
        for (GameDataSimple game : sortedGames) {
            outStr.append(String.format("%-" + idWidth + "d", counter));
            outStr.append(String.format("%-" + nameWidth + "s", game.gameName()));
            outStr.append(String.format("%-" + userWidth + "s", game.whiteUsername()));
            outStr.append(String.format("%-" + userWidth + "s", game.blackUsername()));
            outStr.append("\n");
            counter++;
        }

        outStr.append("----------------------------------------------------------------------------------\n");

        return outStr.toString();
    }



    public String help() {

        return switch (state) {
            case SIGNEDOUT -> """
                    * -l login <USERNAME> <PASSWORD>>
                    * -r register <USERNAME> <PASSWORD>> <EMAIL>
                    * -h help
                    * -q quit
                    """;
            case SIGNEDIN -> """
                    * -j join <GAME #> [WHITE|BLACK|OBSERVER] (Default=OBSERVER)
                    * -l list-games
                    * -c create <NAME>
                    * -q logout
                    * -h help
                    * -q quit
                    """;
            case GAMEPLAY -> """
                    - {To Impliment in Phase 6}
                    - help
                    - quit
                    """;
            case OBSERVER -> """
                    """;
        };
    }


}
