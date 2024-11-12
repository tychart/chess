import chess.*;
import client.Repl;
import exception.ResponseException;
import model.LoginResponse;
import model.UserData;
import server.ServerFacade;

import java.awt.*;

public class Main {
    public static void main(String[] args) throws ResponseException {
//        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
//        System.out.println("♕ 240 Chess Client: " + piece);
        System.out.println("Started Program");

        String serverUrl = "http://localhost:8080";

        if (args.length == 1) {
            serverUrl = args[0];
        }

        new Repl(serverUrl).run();


//        ServerFacade serverFacade = new ServerFacade("http://localhost:8080");
//
//        UserData newUser = new UserData("tychart", "badpass", "tyler@byu");
//
////        LoginResponse authStuff = serverFacade.registerUser(newUser);
//
//        LoginResponse authStuff = serverFacade.loginUser(newUser);
//        String currToken = authStuff.authToken();
//        System.out.println(authStuff);
//        serverFacade.logoutUser(currToken);
////        serverFacade.deleteDatabase(currToken);
//
////        serverFacade.logoutUser(currToken);
//
//
//
////        System.out.println(String.valueOf('A')).ordinal();
//
//        ChessGame game = new ChessGame();
//        ChessBoard board = new ChessBoard();
//
//        ChessPosition currPos = new ChessPosition(5, 4);
//
//        board.addPiece(currPos, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
//        System.out.println(board);
//
//        ChessPiece currPiece = board.getPiece(currPos);
//
//        board.resetBoard();
//
//        System.out.println(board);

//        System.out.println(currPiece);
//        System.out.println(currPiece.pieceMoves(board, currPos));

        System.out.println("Finished Program");
    }
}