package websocket.messages;

public class NotificationMessage extends ServerMessage {
    private String message;

    public NotificationMessage(String message) {
        super(ServerMessageType.NOTIFICATION); // Call the parent constructor with the Notification type
        this.message = message;
    }

    // Getter for the game field
    public String getMessage() {
        return message;
    }

    // Setter for the game field
    public void setMessage(String game) {
        this.message = message;
    }
}