package client;

import com.google.gson.Gson;
import exception.ResponseException;

import websocket.commands.*;
import websocket.messages.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;
    Gson gson = new Gson();


    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;


            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = gson.fromJson(message, ServerMessage.class);
                    switch (notification.getServerMessageType()) {
                        case NOTIFICATION -> notificationHandler.notify(gson.fromJson(message, NotificationMessage.class));
                        case ERROR -> notificationHandler.error(gson.fromJson(message, ErrorMessage.class));
                        case LOAD_GAME -> notificationHandler.loadGame(gson.fromJson(message, LoadGameMessage.class));
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connectWebSocket(String authToken, int gameID) throws ResponseException {
        try {
            ConnectCommand connectCommand = new ConnectCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(gson.toJson(connectCommand));
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

//    public void enterPetShop(String visitorName) throws ResponseException {
//        try {
//            var action = new Action(Action.Type.ENTER, visitorName);
//            this.session.getBasicRemote().sendText(new Gson().toJson(action));
//        } catch (IOException ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }
//
//    public void leavePetShop(String visitorName) throws ResponseException {
//        try {
//            var action = new Action(Action.Type.EXIT, visitorName);
//            this.session.getBasicRemote().sendText(new Gson().toJson(action));
//            this.session.close();
//        } catch (IOException ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }

}

