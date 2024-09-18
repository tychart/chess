package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] squares;

    public ChessBoard() {
        this.squares = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()][position.getColumn()] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()][position.getColumn()];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        this.squares = new ChessPiece[8][8];

        setSide(ChessGame.TeamColor.WHITE);
        setSide(ChessGame.TeamColor.BLACK);
    }

    private void setSide(ChessGame.TeamColor color) {
        int backRow;
        int frontRow;
        if (color == ChessGame.TeamColor.WHITE) {
            backRow = 1;
            frontRow = 2;
        } else {
            backRow = 8;
            frontRow = 7;
        }

        addPiece(new ChessPosition(backRow, 1), new ChessPiece(color, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(backRow, 2), new ChessPiece(color, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(backRow, 3), new ChessPiece(color, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(backRow, 4), new ChessPiece(color, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(backRow, 5), new ChessPiece(color, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(backRow, 6), new ChessPiece(color, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(backRow, 7), new ChessPiece(color, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(backRow, 8), new ChessPiece(color, ChessPiece.PieceType.ROOK));

        for (int i = 1; i <= 8; i++) {
            addPiece(new ChessPosition(frontRow, i), new ChessPiece(color, ChessPiece.PieceType.PAWN));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
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

