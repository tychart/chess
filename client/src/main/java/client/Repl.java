package client;

import java.util.Scanner;

import static client.EscapeSequences.*;

public class Repl {
    private final ChessClient client;

    public Repl(String serverUrl){
        this.client = new ChessClient(serverUrl);
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
                System.out.print(BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

//    public void notify(Notification notification) {
//        System.out.println(RED + notification.message());
//        printPrompt();
//    }

    private void printPrompt() {
        System.out.print("\n" + RESET + ">>> " + GREEN);
    }
}
