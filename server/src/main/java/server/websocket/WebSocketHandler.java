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
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;

import chess.*;

import java.io.IOException;
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

//            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);

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
                case LEAVE -> leave(session, command, currUser);
            }
        } catch (ServiceException e) {
            System.out.println(e.getMessage());
            ServerMessage serverError = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            serverError.addMessage(e.getMessage());
            session.getRemote().sendString(new Gson().toJson(serverError));
        }
    }

    private void connect(Session session, UserGameCommand command, UserData currUser) throws IOException, ServiceException {
        connections.add(command.getAuthToken(), session);
        var message = String.format("%s has joined game %d", currUser.username(), command.getGameID());
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        serverMessage.addMessage(message);
        connections.broadcastAllButSelf(command.getAuthToken(), serverMessage);


        ServerMessage loadGameServerMessage = new LoadGameMessage(new Gson().toJson(dataAccess.getGame(command.getGameID()).game()));
        connections.broadcastSelf(command.getAuthToken(), loadGameServerMessage);
    }

    private void makeMove(Session session, MakeMoveCommand command, UserData currUser) throws IOException {

        try {
            GameData gameData = dataAccess.getGame(command.getGameID());

            ChessGame chessGame = gameData.game();

            chessGame.makeMove(command.getChessMove());

            dataAccess.addGame(new GameData(
                    gameData.gameID(),
                    gameData.whiteUsername(),
                    gameData.blackUsername(),
                    gameData.gameName(),
                    chessGame
            ));

            ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            serverMessage.addMessage(new Gson().toJson(chessGame));

            connections.broadcastAll(serverMessage);

        } catch (Exception e) {
            System.out.println("SOMETHING TERRIBLE HAPPENED: " + e.getMessage());
            ServerMessage serverError = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            serverError.addMessage(e.getMessage());
            connections.broadcastSelf(command.getAuthToken(), serverError);
        }
    }

//    private GameData getUserGame(String username) throws ServiceException {
//        Map<Integer, GameData> games = dataAccess.getAllGames();
//
//        for (Integer gameID : games.keySet()) {
//            GameData gameData = games.get(gameID);
//
//            if (
//                    Objects.equals(gameData.whiteUsername(), username) ||
//                            Objects.equals(gameData.blackUsername(), username)
//            ) {
//                return dataAccess.getGame(gameID);
//            }
//        }
//        throw new ServiceException("Error: Unable to find game for user " + username);
//    }

    private void leave(Session session, UserGameCommand command, UserData currUser) throws IOException {
        connections.remove(command.getAuthToken());
        var message = String.format("%s has left game %d", currUser.username(), command.getGameID());
        var serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        serverMessage.addMessage(message);
        connections.broadcastAllButSelf(command.getAuthToken(), serverMessage);
    }


//    private void enter(String visitorName, Session session) throws IOException {
//        connections.add(visitorName, session);
//        var message = String.format("%s is in the shop", visitorName);
//        var notification = new Notification(Notification.Type.ARRIVAL, message);
//        connections.broadcast(visitorName, notification);
//    }
//
//    private void exit(String visitorName) throws IOException {
//        connections.remove(visitorName);
//        var message = String.format("%s left the shop", visitorName);
//        var notification = new Notification(Notification.Type.DEPARTURE, message);
//        connections.broadcast(visitorName, notification);
//    }
//
//    public void makeNoise(String petName, String sound) throws ResponseException {
//        try {
//            var message = String.format("%s says %s", petName, sound);
//            var notification = new Notification(Notification.Type.NOISE, message);
//            connections.broadcast("", notification);
//        } catch (Exception ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }
}