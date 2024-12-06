package client;

import java.util.*;

import chess.*;
import exception.ResponseException;
import model.*;
import ui.TestUIPrint;


public class ChessClient {
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;
    private String authToken = null;
    private final NotificationHandler notificationHandler;
    private WebSocketFacade wsf;
    private ChessGame localGame = null;
    private Integer currGameID = null;
    private ChessGame.TeamColor currTeamColor = null;

    public ChessClient(String serverUrl, NotificationHandler notificationHandler) {
        this.serverUrl = serverUrl;
        this.server = new ServerFacade(this.serverUrl);
        this.notificationHandler = notificationHandler;
    }

    public String eval(String input, ChessGame latestCachedGame) throws ResponseException {
        this.localGame = latestCachedGame;
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);

        return switch (state) {
            case SIGNEDOUT -> unauthenicatedSwitch(params, cmd);
            case SIGNEDIN -> authenicatedSwitch(params, cmd);
            case GAMEPLAY -> gameplaySwitch(params, cmd);
            case OBSERVER -> observerSwitch(params, cmd);
            case CONFIRM -> confirmSwitch(params, cmd);
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
            case "move", "-m" -> makeMove(params);
            case "redraw", "-r" -> redrawBoard(this.localGame);
            case "highlight", "-l" -> highlightMoves(params);
            case "leave", "-q" -> leaveGame();
            case "resign" -> askToResign();
            default -> help();
        };
    }


    private String observerSwitch(String[] params, String cmd) throws ResponseException {
        return switch (cmd) {
            case "redraw", "-r" -> redrawBoard(this.localGame);
            case "highlight", "-l" -> highlightMoves(params);
            case "leave", "-q" -> leaveGame();
            default -> help();
        };
    }

    private String confirmSwitch(String[] params, String cmd) throws ResponseException {
        return switch (cmd) {
            case "resign", "y", "yes" -> resignGame(params);
            default -> doNotResign();
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
            throw new ResponseException(e.getStatusCode(), "Login failed.  " + e.getMessage());
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
            throw new ResponseException(e.getStatusCode(), "Registration failed: " + e.getMessage());
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
        try {
            gameIndex = Integer.parseInt(params[0]) - 1;
            List<GameDataSimple> sortedGames = getSortedGameList();
            this.currGameID = sortedGames.get(gameIndex).gameID();
            gameName = sortedGames.get(gameIndex).gameName();
        } catch (NumberFormatException e) {
            throw new ResponseException(400, "Error: Invalid Game index format: " + params[0]);
        } catch (IndexOutOfBoundsException e) {
            throw new ResponseException(400, "Error: Game index is out of range.");
        }

        // Handle observer case
        if (params.length == 1 || "OBSERVER".equalsIgnoreCase(params[1])) {
            JoinGameRequest joinGameRequest = new JoinGameRequest(null, this.currGameID); // null for observer
//            server.joinGame(authToken, joinGameRequest);
            wsf = new WebSocketFacade(serverUrl, notificationHandler);
            wsf.connectWebSocket(authToken, this.currGameID);
//            displayTestBoards();
            state = State.OBSERVER;
            return "Successfully joined game " + gameName + " as an observer.";
        }

        // Handle player case
        try {
            currTeamColor = parseTeamColor(params[1]);
            JoinGameRequest joinGameRequest = new JoinGameRequest(currTeamColor, this.currGameID);
            server.joinGame(authToken, joinGameRequest);
            wsf = new WebSocketFacade(serverUrl, notificationHandler);
            wsf.connectWebSocket(authToken, this.currGameID);
            state = State.GAMEPLAY;
//            displayTestBoards();
            return "Successfully joined game " + gameName + " as " + currTeamColor;

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
            throw new ResponseException(e.getStatusCode(), "Failed to create game: " + e.getMessage());
        }
    }

    public String makeMove(String[] params) throws ResponseException {
        if (params.length < 1) {
            throw new ResponseException(400, "Invalid input! Expected at least one string");
        }
        ChessPiece.PieceType promotionPiece = null;
        if (params.length > 2) {
            throw new ResponseException(400, "Invalid input! Expected at most 2 strings");
        }

        if (params.length == 2) {
            try {
                promotionPiece = parsePromotionPieceType(params[1]);
            } catch (IllegalArgumentException e) {
                throw new ResponseException(400, "Invalid input! " + e.getMessage());
            }
        }

        if (!this.localGame.isGoing()) {
            throw new ResponseException(400, "Error: Can't move, game is over");
        }

        ChessMove chessMove = parseChessMove(params[0], promotionPiece);

        try {
            this.localGame.makeMove(chessMove);
        } catch (InvalidMoveException e) {
            throw new ResponseException(400, e.getMessage());
        }

        wsf.makeMove(authToken, this.currGameID, chessMove);

        return String.format("Move made '%s'", params[0]);
    }

    public static ChessMove parseChessMove(String moveString, ChessPiece.PieceType promotionPiece) {
        String[] parts = moveString.split("-");

        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid move string: " + moveString);
        }

        ChessPosition startPosition = parsePosition(parts[0]);
        ChessPosition endPosition = parsePosition(parts[1]);

        return new ChessMove(startPosition, endPosition, promotionPiece); // Assuming no promotion
    }

    private static ChessPosition parsePosition(String positionString) {
        // Ensure the string has exactly 2 characters
        if (positionString == null || positionString.length() != 2) {
            throw new IllegalArgumentException("Invalid position string: " + positionString + ". Must be in the format 'e3' or 'f7'.");
        }

        // Extract the column character and row number
        char colChar = positionString.charAt(0);
        char rowChar = positionString.charAt(1);

        // Check that the column is between 'a' and 'h'
        if (colChar < 'a' || colChar > 'h') {
            throw new IllegalArgumentException("Invalid column: " + colChar + ". Must be a letter between 'a' and 'h'.");
        }

        // Check that the row is a digit between '1' and '8'
        if (rowChar < '1' || rowChar > '8') {
            throw new IllegalArgumentException("Invalid row: " + rowChar + ". Must be a number between '1' and '8'.");
        }

        // Convert the row character to an integer
        int row = Character.getNumericValue(rowChar);

        // Convert the column character to uppercase and map to ChessPosition.Column
        ChessPosition.Column column = ChessPosition.Column.valueOf(String.valueOf(colChar).toUpperCase());

        // Return a valid ChessPosition
        return new ChessPosition(column, row);
    }

    /**
     * Parses a string into a valid promotion PieceType enum (QUEEN, BISHOP, KNIGHT, ROOK), ignoring case.
     *
     * @param pieceTypeString The input string representing a chess piece type.
     * @return The corresponding PieceType enum.
     * @throws IllegalArgumentException if the input does not match a valid promotion piece.
     */
    public static ChessPiece.PieceType parsePromotionPieceType(String pieceTypeString) {
        if (pieceTypeString == null || pieceTypeString.trim().isEmpty()) {
            throw new IllegalArgumentException("Input string cannot be null or empty.");
        }

        // Normalize the input string
        String normalizedInput = pieceTypeString.trim().toUpperCase();

        // Validate against allowed promotion pieces
        switch (normalizedInput) {
            case "QUEEN":
                return ChessPiece.PieceType.QUEEN;
            case "BISHOP":
                return ChessPiece.PieceType.BISHOP;
            case "KNIGHT":
                return ChessPiece.PieceType.KNIGHT;
            case "ROOK":
                return ChessPiece.PieceType.ROOK;
            default:
                throw new IllegalArgumentException(
                        "Invalid promotion piece type: '" + pieceTypeString +
                                "'. Valid options are: QUEEN, BISHOP, KNIGHT, ROOK."
                );
        }
    }



    public String redrawBoard(ChessGame currGame) {
        TestUIPrint printBoard = new TestUIPrint(currGame);

        if (currTeamColor == ChessGame.TeamColor.BLACK) {
            printBoard.drawFlippedChessBoard(new HashSet<>());
        } else {
            printBoard.drawNormalChessBoard(new HashSet<>());
        }

        return "";
    }

    public String highlightMoves(String[] params) throws ResponseException {
        if (params.length != 1) {
            throw new ResponseException(400, "Invalid input! Expected one string without spaces");
        }
        TestUIPrint printBoard = new TestUIPrint(this.localGame);

        ChessPosition currPos = parsePosition(params[0]);

        Collection<ChessMove> validMoves = this.localGame.validMoves(currPos);

        if (validMoves == null) {
            validMoves = new HashSet<>();
        }

        if (currTeamColor == ChessGame.TeamColor.BLACK) {
            printBoard.drawFlippedChessBoard(validMoves);
        } else {
            printBoard.drawNormalChessBoard(validMoves);
        }

        return "";
    }

    public String leaveGame() throws ResponseException {
        wsf.sendLeave(this.authToken, this.currGameID);
        state = State.SIGNEDIN;
        return "Leaving the game";
    }

    public String askToResign() {
        state = State.CONFIRM;
        return help();
    }

    public String doNotResign() {
        state = State.GAMEPLAY;
        return help();
    }

    public String resignGame(String[] params) throws ResponseException {
        wsf.resign(this.authToken, this.currGameID);
        return "Resigning the game, you loose";
    }

    private ChessGame.TeamColor parseTeamColor(String color) {
        if (color.length() == 1) {
            if (color.equalsIgnoreCase("W")) {
                return ChessGame.TeamColor.WHITE;
            }
            if (color.equalsIgnoreCase("B")) {
                return ChessGame.TeamColor.BLACK;
            }
        }
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
                    * -r redraw
                    * -l highlight <letter><number> # Highlights possible moves for selected piece. Example: highlight e2
                    * -m move <start letter><start number>-<dest letter><dest number> [QUEEN|BISHOP|KNIGHT|ROOK] # Example: move e2-e4 KNIGHT # Promotion if applicable
                    * resign
                    * -h help
                    * -q leave
                    """;
            case OBSERVER -> """
                    * -r redraw
                    * -l highlight <letter><number> # Highlights possible moves for selected piece. Example: highlight e2
                    * -h help
                    * -q leave
                    """;
            case CONFIRM -> """
                    Are you absolutely sure you want to resign the game?
                    * [y|yes]
                    * [n|no]
                    """;
        };
    }


}
