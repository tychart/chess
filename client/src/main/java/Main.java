import chess.*;

public class Main {
    public static void main(String[] args) {
//        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
//        System.out.println("♕ 240 Chess Client: " + piece);
        System.out.println("Started Program");

//        System.out.println(String.valueOf('A')).ordinal();

        ChessGame game = new ChessGame();
        ChessBoard board = new ChessBoard();

        ChessPosition currPos = new ChessPosition(5, 4);

        board.addPiece(currPos, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        System.out.println(board);

        ChessPiece currPiece = board.getPiece(currPos);

        System.out.println(currPiece);
        System.out.println(currPiece.pieceMoves(board, currPos));

        System.out.println("Finished Program");
    }
}