package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {

        this.pieceColor = pieceColor;
        this.type = type;

    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
//        throw new RuntimeException("Not implemented");

        return this.pieceColor;

    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
//        throw new RuntimeException("Not implemented");

        return this.type;

    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
//        throw new RuntimeException("Not implemented");
        Collection<ChessMove> return_collection = new HashSet<>();

        switch (this.type) {
            case KING -> {
                return this.movesKing(return_collection, board, myPosition);
            }
            case QUEEN -> {
                return this.movesQueen();
            }

        }

    }

    private Collection<ChessMove> movesKing(Collection<ChessMove> return_set, ChessBoard board, ChessPosition myPosition) {
//        return Collection(new ChessMove(new ChessPosition(1,2), new ChessPosition(4, 5), this.type);


    }

    private Collection<ChessMove> movesQueen(ChessBoard board, ChessPosition myPosition) {
        return true;
    }

    private Collection<ChessMove> movesBishop(ChessBoard board, ChessPosition myPosition) {
        return true;
    }

    private Collection<ChessMove> movesKnight() {
        return true;
    }

    private Collection<ChessMove> movesRook() {
        return true;
    }

    private Collection<ChessMove> movesPawn() {
        return true;
    }

    private Collection<ChessMove> searchUp(Collection<ChessMove> return_set, ChessBoard board, ChessPosition myPosition) {

        ChessPosition new_pos = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
        if (board.getPiece(new_pos) != null) {

        }
    }

    @Override
    public String toString() {
        return "[" + this.pieceColor + " " + this.type + "]";
    }
}
