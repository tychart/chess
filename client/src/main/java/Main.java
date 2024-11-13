import client.Repl;


public class Main {
    public static void main(String[] args) {
        System.out.println("Started Program");

        String serverUrl = "http://localhost:8080";

        if (args.length == 1) {
            serverUrl = args[0];
        }

        new Repl(serverUrl).run();

        System.out.println("Finished Program");
    }
}