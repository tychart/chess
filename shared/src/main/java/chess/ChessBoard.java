package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] squares = new ChessPiece[8][8];


    public ChessBoard() {


    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {

        squares[position.getRow()][position.getColumn()] = piece;

//        throw new RuntimeException("Not implemented");
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {

        //return true; // Continue Implimenting

//        throw new RuntimeException("Not implemented");

        return squares[position.getRow()][position.getColumn()];

    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String toString() {
        StringBuilder outStr = new StringBuilder("ChessBoard[\n    ");

        for (int i = squares.length - 1; i >= 0; i--) {
            outStr.append(i + 1);
            outStr.append(" ");
            for (int j = 0; j < squares[i].length; j++) {
                ChessPiece newEntry = squares[i][j];
                if (newEntry == null) {
                    outStr.append(" -- ");
                } else {
                    outStr.append(newEntry.toString());
                }
            }
            outStr.append("\n");
            if (i > 0) {
                outStr.append("    ");
            }
        }

        outStr.append("       1   2   3   4   5   6   7   8\n");

        return outStr.append("]").toString();
    }
}

