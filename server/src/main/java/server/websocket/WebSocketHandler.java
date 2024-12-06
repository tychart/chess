package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.protobuf.ServiceException;
import exception.ResponseException;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
//import websocket.commands.Action;

import dataaccess.DataAccess;
import websocket.commands.ConnectCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import chess.*;

import java.io.IOException;
import java.rmi.ServerError;
import java.util.Map;
import java.util.Objects;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final DataAccess dataAccess;
    Gson gson = new Gson();

    public WebSocketHandler(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try {
            UserGameCommand command = null;

            // Try parsing as specific command types
            try {
                command = gson.fromJson(message, MakeMoveCommand.class); // Try ConnectCommand
                if (command.getCommandType() != UserGameCommand.CommandType.MAKE_MOVE) {
                    command = gson.fromJson(message, UserGameCommand.class);
                }
            } catch (JsonSyntaxException ignored) {}


            UserData currUser = dataAccess.authenticateUser(command.getAuthToken());

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, command, currUser);
                case MAKE_MOVE -> makeMove(session, (MakeMoveCommand) command, currUser);
                case RESIGN -> resign(session, command, currUser);
                case LEAVE -> leave(session, command, currUser);
            }
        } catch (ServiceException e) {
            System.out.println(e.getMessage());
            ServerMessage serverError = new ErrorMessage(e.getMessage());
            session.getRemote().sendString(new Gson().toJson(serverError));
        }
    }

    private void connect(Session session, UserGameCommand command, UserData currUser) throws ServiceException {
        GameData gameData = dataAccess.getGame(command.getGameID());
        connections.add(command.getGameID(), command.getAuthToken(), session);

        String playerType;
        System.out.printf("CurrUsername '%s', whiteUsername '%s', blackUsername '%s'", currUser.username(), gameData.whiteUsername(), gameData.blackUsername());
        if (Objects.equals(gameData.whiteUsername(), currUser.username())) {
            playerType = "White";
        } else if (Objects.equals(gameData.blackUsername(), currUser.username())) {
            playerType = "Black";
        } else {
            playerType = "Observer";
        }
        System.out.printf("Chose %s", playerType);

        var message = String.format("%s has joined game %d as %s", currUser.username(), command.getGameID(), playerType);
        var serverMessage = new NotificationMessage(message);
        connections.broadcastAllButSelf(command.getGameID(), command.getAuthToken(), serverMessage);


        ServerMessage loadGameServerMessage = new LoadGameMessage(new Gson().toJson(dataAccess.getGame(command.getGameID()).game()));
        connections.broadcastSelf(command.getGameID(), command.getAuthToken(), loadGameServerMessage);
    }

    private void makeMove(Session session, MakeMoveCommand command, UserData currUser) throws ServiceException {

        try {
            GameData gameData = dataAccess.getGame(command.getGameID());

            ChessGame chessGame = gameData.game();
            ChessGame.TeamColor pieceColor = chessGame.getBoard().getPiece(command.getChessMove().getStartPosition()).getTeamColor();

            switch (pieceColor) {
                case WHITE:
                    if (!Objects.equals(currUser.username(), gameData.whiteUsername())) {
                        throw new InvalidMoveException("Error: Don't move the other player's piece!");
                    }
                    break;
                case BLACK:
                    if (!Objects.equals(currUser.username(), gameData.blackUsername())) {
                        throw new InvalidMoveException("Error: Don't move the other player's piece!");
                    }
                    break;
                default:
                    throw new InvalidMoveException("Error: Not moving a valid piece");
            }

            chessGame.makeMove(command.getChessMove());
            ChessGame.TeamColor userColor;

            if (Objects.equals(gameData.whiteUsername(), currUser.username())) {
                userColor = ChessGame.TeamColor.WHITE;
            } else {
                userColor = ChessGame.TeamColor.BLACK;
            }

            // Check to see if either player is in checkmate
            if (
                    chessGame.isInCheckmate(chessGame.getTeamTurn()) ||
                    chessGame.isInCheckmate(ChessGame.getOpposingTeamColor(chessGame.getTeamTurn()))
            ) {
                String winningUser;
                String loosingUser;
                if (chessGame.getTeamTurn() == userColor) { // The current user lost
                    if (userColor == ChessGame.TeamColor.WHITE) {
                        winningUser = gameData.blackUsername();
                        loosingUser = gameData.whiteUsername();
                    } else {
                        winningUser = gameData.whiteUsername();
                        loosingUser = gameData.blackUsername();
                    }
                } else { // The current user won
                    if (userColor == ChessGame.TeamColor.WHITE) {
                        winningUser = gameData.whiteUsername();
                        loosingUser = gameData.blackUsername();
                    } else {
                        winningUser = gameData.blackUsername();
                        loosingUser = gameData.whiteUsername();
                    }
                }
                ServerMessage endGameNotification = new NotificationMessage("User " + loosingUser + " has been checkmated. User " + winningUser + " has won! Congratulations to " + winningUser);

                connections.broadcastAll(command.getGameID(), endGameNotification);
            }

            // Check if either player is in check
            if (chessGame.isGoing()) {
                if (chessGame.isInCheck(userColor)) {
                    String checkUser = "";
                    if (userColor == ChessGame.TeamColor.WHITE) {
                        checkUser = gameData.whiteUsername();
                    } else {
                        checkUser = gameData.blackUsername();
                    }

                    ServerMessage checkNotification = new NotificationMessage("User " + checkUser + " is in check!");
                    connections.broadcastAll(command.getGameID(), checkNotification);
                } else if (chessGame.isInCheck(ChessGame.getOpposingTeamColor(userColor))) {
                    String checkUser = "";
                    if (userColor == ChessGame.TeamColor.WHITE) {
                        checkUser = gameData.blackUsername();
                    } else {
                        checkUser = gameData.whiteUsername();
                    }
                    ServerMessage checkNotification = new NotificationMessage("User " + checkUser + " is in check!");
                    connections.broadcastAll(command.getGameID(), checkNotification);
                }

                if (chessGame.isInStalemate(chessGame.getTeamTurn()) ||
                        chessGame.isInStalemate(ChessGame.getOpposingTeamColor(chessGame.getTeamTurn()))
                ) {
                    ServerMessage endGameNotification = new NotificationMessage("Stalemate, unfortunately everyone looses");
                    connections.broadcastAll(command.getGameID(), endGameNotification);
                }
            }


            dataAccess.addGame(new GameData(
                    gameData.gameID(),
                    gameData.whiteUsername(),
                    gameData.blackUsername(),
                    gameData.gameName(),
                    chessGame
            ));

            ServerMessage loadGameMessage = new LoadGameMessage(new Gson().toJson(chessGame));
            connections.broadcastAll(command.getGameID(), loadGameMessage);

            ServerMessage notificationMessage = new NotificationMessage("User" + currUser.username() + " made the move " + command.getChessMove());
            connections.broadcastAllButSelf(command.getGameID(), command.getAuthToken(), notificationMessage);

        } catch (InvalidMoveException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    private void resign(Session session, UserGameCommand command, UserData currUser) throws ServiceException {
            GameData gameData = dataAccess.getGame(command.getGameID());
            if (!gameData.game().isGoing()) {
                throw new ServiceException("Error: If the game is not going, then you can not resign");
            }
            if (!Objects.equals(currUser.username(), gameData.whiteUsername()) && !Objects.equals(currUser.username(), gameData.blackUsername())) {
                throw new ServiceException("Error: If you are not playing, you can not resign");
            }

            gameData.game().resign();
            dataAccess.addGame(gameData);

            ServerMessage serverMessage = new NotificationMessage("User " + currUser.username() + " has resigned, thus the game is over");
            connections.broadcastAll(command.getGameID(), serverMessage);

    }

    private void leave(Session session, UserGameCommand command, UserData currUser) throws ServiceException {
        connections.remove(command.getGameID(), command.getAuthToken());
        var message = String.format("%s has left game %d", currUser.username(), command.getGameID());
        var serverMessage = new NotificationMessage(message);

        // Remove the player from the current game
        GameData gameData = dataAccess.getGame(command.getGameID());
        if (Objects.equals(currUser.username(), gameData.whiteUsername())) {
            dataAccess.addGame(new GameData(
                    gameData.gameID(),
                    null,
                    gameData.blackUsername(),
                    gameData.gameName(),
                    gameData.game()
            ));
        } else if (Objects.equals(currUser.username(), gameData.blackUsername())) {
            dataAccess.addGame(new GameData(
                    gameData.gameID(),
                    gameData.whiteUsername(),
                    null,
                    gameData.gameName(),
                    gameData.game()
            ));
        }
        connections.broadcastAllButSelf(command.getGameID(), command.getAuthToken(), serverMessage);
    }

}