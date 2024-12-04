package websocket.messages;

public class ErrorMessage extends ServerMessage {
    private String errorMessage;

    public ErrorMessage(String errorMessage) {
        super(ServerMessageType.ERROR); // Call the parent constructor with the ERROR type
        this.errorMessage = errorMessage;
    }

    // Getter for the game field
    public String getErrorMessage() {
        return errorMessage;
    }

    // Setter for the game field
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}