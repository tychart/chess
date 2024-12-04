package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    private String userAuthtoken;
    private Session session;

    public Connection(String userAuthtoken, Session session) {
        this.userAuthtoken = userAuthtoken;
        this.session = session;
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }

    public String getAuthtoken() {
        return this.userAuthtoken;
    }

    public Session getSession() {
        return this.session;
    }
}