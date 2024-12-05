package client;

import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Scanner;

import static client.EscapeSequences.*;

public class Repl implements NotificationHandler{
    private final ChessClient client;

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
                result = client.eval(line);
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
        System.out.println(RED + notification.getGame());
        printPrompt();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET + ">>> " + GREEN);
    }
}
