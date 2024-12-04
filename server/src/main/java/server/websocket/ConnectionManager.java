package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
//import webSocketMessages.Notification;
import websocket.messages.ServerMessage;


import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import com.google.gson.Gson;
import java.util.function.Predicate;


public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String userAuthToken, Session session) {
        var connection = new Connection(userAuthToken, session);
        connections.put(userAuthToken, connection);
    }

    public void remove(String visitorName) {
        connections.remove(visitorName);
    }

    public void broadcastAll(ServerMessage serverMessage) {
        this.broadcast(connection -> true, serverMessage);
    }

    public void broadcastAllButSelf(String sendingUserAuthToken, ServerMessage serverMessage) {
        this.broadcast(
                connection -> !connection.getAuthtoken().equals(sendingUserAuthToken),
                serverMessage
        );
    }

    public void broadcastSelf(String sendingUserAuthToken, ServerMessage serverMessage) {
        this.broadcast(
                connection -> connection.getAuthtoken().equals(sendingUserAuthToken),
                serverMessage
        );
    }

    public void broadcast(Predicate<Connection> shouldBroadcast, ServerMessage serverMessage) {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
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
