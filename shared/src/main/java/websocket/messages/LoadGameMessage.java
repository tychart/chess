package websocket.messages;

public class LoadGameMessage extends ServerMessage {
    private String game;

    public LoadGameMessage(String game) {
        super(ServerMessageType.LOAD_GAME); // Call the parent constructor with the LOAD_GAME type
        this.game = game;
    }

    // Getter for the game field
    public String getGame() {
        return game;
    }

    // Setter for the game field
    public void setGame(String game) {
        this.game = game;
    }
}