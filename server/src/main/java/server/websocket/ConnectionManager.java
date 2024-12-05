package server.websocket;

import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
//import webSocketMessages.Notification;
import websocket.messages.ServerMessage;


import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import com.google.gson.Gson;
import java.util.function.Predicate;


public class ConnectionManager {
    public final ConcurrentHashMap<Integer, ConcurrentHashMap<String, Connection>> gameConnections = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(int gameID, String userAuthToken, Session session) {
        var connection = new Connection(userAuthToken, session);

        // Compute or get the map for the specific gameID
        var gameConnectionMap = gameConnections.computeIfAbsent(gameID, k -> new ConcurrentHashMap<>());

        // Add the connection to the game's connection map
        gameConnectionMap.put(userAuthToken, connection);
    }

    public void remove(int gameID, String visitorName) {
        var gameConnectionMap = gameConnections.get(gameID);

        if (gameConnectionMap != null) {
            gameConnectionMap.remove(visitorName);
        }
    }

    public void broadcastAll(int gameID, ServerMessage serverMessage) {
        this.broadcast(gameID, connection -> true, serverMessage);
    }

    public void broadcastAllButSelf(int gameID, String sendingUserAuthToken, ServerMessage serverMessage) {
        this.broadcast(
                gameID,
                connection -> !connection.getAuthtoken().equals(sendingUserAuthToken),
                serverMessage
        );
    }

    public void broadcastSelf(int gameID, String sendingUserAuthToken, ServerMessage serverMessage) {
        this.broadcast(
                gameID,
                connection -> connection.getAuthtoken().equals(sendingUserAuthToken),
                serverMessage
        );
    }

    public void broadcast(int gameID, Predicate<Connection> shouldBroadcast, ServerMessage serverMessage) {
        var gameConnectionMap = gameConnections.get(gameID);
        if (gameConnectionMap == null) {
            return; // No connections for the gameID, nothing to broadcast
        }

        var removeList = new ArrayList<Connection>();
        for (var c : gameConnectionMap.values()) {
            if (c.getSession().isOpen()) {
                if (shouldBroadcast.test(c)) {
                    try {
                        c.send(new Gson().toJson(serverMessage));
                    } catch (IOException e) {
                        removeList.add(c);
                    }
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.getAuthtoken());
        }
    }
}
