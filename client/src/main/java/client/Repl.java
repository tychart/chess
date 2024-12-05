package client;

import chess.ChessGame;
import com.google.gson.Gson;
import ui.TestUIPrint;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Scanner;

import static client.EscapeSequences.*;

public class Repl implements NotificationHandler{
    private final ChessClient client;
    private ChessGame localGame = null;

    public Repl(String serverUrl){
        this.client = new ChessClient(serverUrl, this);
    }

    public void run() {
        System.out.println("\u2655 Welcome to the Chess Program. Please sign in to start.");
        System.out.print("Please enter -h for help or -q to quit");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line, localGame);
                System.out.print(BLUE + result + RESET);
            } catch (Throwable e) {
                var msg = e.getMessage();
                System.out.print(RED + msg + RESET);
            }
        }
        System.out.println();
    }

    public void notify(NotificationMessage notification) {
        System.out.println(BLUE + notification.getMessage());
        printPrompt();
    }

    public void error(ErrorMessage notification) {
        System.out.println(RED + notification.getErrorMessage());
        printPrompt();
    }

    public void loadGame(LoadGameMessage notification) {
//        System.out.println(RED + notification.getGame());
        this.localGame = new Gson().fromJson(notification.getGame(), ChessGame.class);
        client.redrawBoard(this.localGame);
        printPrompt();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET + ">>> " + GREEN);
    }
}
