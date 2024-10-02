package chess;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.HashMap;
import java.util.Map;

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

    // Copy constructor
    public ChessBoard(ChessBoard other) {
        this.squares = new ChessPiece[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (other.squares[i][j] != null) {
                    this.squares[i][j] = new ChessPiece(other.squares[i][j]);
                }
            }
        }
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

    public ChessPiece removePiece(ChessPosition position) {
        ChessPiece removedPiece = squares[position.getRow()][position.getColumn()];
        squares[position.getRow()][position.getColumn()] = null;
        return removedPiece;
    }

    // This is specifically for moving pieces when testing configurations for potential moves
    public void unsafeMovePiece(ChessMove move) {
        ChessPiece currPiece = removePiece(move.getStartPosition());
        addPiece(move.getEndPosition(), currPiece);
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

    public HashSet<ChessPosition> getAllTeamPieces(ChessGame.TeamColor teamColor) {
        HashSet<ChessPosition> positions = new HashSet<>();


        for (int i = squares.length - 1; i >= 0; i--) {
            for (int j = 0; j < squares[i].length; j++) {
                if (squares[i][j] != null) {
                    if (squares[i][j].getTeamColor() == teamColor) {
                        positions.add(new ChessPosition(i + 1, j + 1));
                    }
                }
            }
        }
        return positions;
    }


    // Given a team color, is that king in check?
    public boolean king_in_check(ChessGame.TeamColor teamColor) {
        ChessGame.TeamColor opponentTeamColor = ChessGame.getOpposingTeamColor(teamColor);
        HashSet<ChessPosition> opponentPieces = this.getAllTeamPieces(opponentTeamColor);
        HashSet<ChessMove> allMoves = new HashSet<ChessMove>();
        HashSet<ChessPosition> allEndPositions = new HashSet<ChessPosition>();
        ChessPosition kingPosition = this.getKingPosition(teamColor);

        for (ChessPosition position : opponentPieces) {
            ChessPiece piece = this.getPiece(position);
            allMoves.addAll(piece.pieceMoves(this, position));
        }

        for (ChessMove move : allMoves) {
            allEndPositions.add(move.getEndPosition());
        }

        return allEndPositions.contains(kingPosition);
    }

    public ChessPosition getKingPosition(ChessGame.TeamColor teamColor) {
        for (int i = squares.length - 1; i >= 0; i--) {
            for (int j = 0; j < squares[i].length; j++) {
                if (squares[i][j] != null) {
                    if (squares[i][j].getTeamColor() == teamColor && squares[i][j].getPieceType() == ChessPiece.PieceType.KING) {
                        return new ChessPosition(i + 1, j + 1);
                    }
                }
            }
        }
        throw new RuntimeException("No King?");
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

