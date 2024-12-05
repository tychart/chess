package client;

import websocket.messages.ServerMessage;
import websocket.messages.ErrorMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.LoadGameMessage;

public interface NotificationHandler {
    void notify(NotificationMessage notificationMessage);

    void error(ErrorMessage errorMessage);

    void loadGame(LoadGameMessage loadGameMessage);
}
