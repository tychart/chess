package client;

import java.util.Arrays;
import com.google.gson.Gson;

import exception.ResponseException;
import model.*;
import server.ServerFacade;


public class ChessClient {
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;

    public ChessClient(String serverUrl) {
        this.serverUrl = serverUrl;
        this.server = new ServerFacade(this.serverUrl);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
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
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String unauthenicated(var params, String cmd) throws ResponseException {
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

    public String login(String... params) throws ResponseException {
        if (params.length == 2) {

            // Extract individual parameters by index
            String username = params[0];
            String password = params[1];

            UserData loginUser = new UserData(username, password, null);
            server.loginUser(loginUser);

            state = State.SIGNEDIN;


            return String.format("Welcome %s! You are signed in, now you can join a game!", username);
        }
        throw new ResponseException(400, "Expected: <username> <password>");
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {

            // Extract individual parameters by index
            String username = params[0];
            String password = params[1];
            String email = params[2];

            // Create a new UserData object with these parameters
            UserData newUser = new UserData(username, password, email);
            server.registerUser(newUser);

            state = State.SIGNEDIN;
            return String.format("Welcome %s! You are signed in, now you can join a game!", username);
        }
        throw new ResponseException(400, "Expected: <username> <password> <email>");
    }


    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - login <username> <password>
                    - register <username> <password> <email>
                    - help
                    - quit
                    """;
        }
        return """
                - list
                - adopt <pet id>
                - rescue <name> <CAT|DOG|FROG|FISH>
                - adoptAll
                - signOut
                - quit
                """;
    }


}
