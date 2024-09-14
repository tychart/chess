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
    private final PieceType type;
    Collection<ChessMove> movesPossible = new HashSet<>();

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {

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

    private enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT,
        UPLEFT,
        UPRIGHT,
        DOWNLEFT,
        DOWNRIGHT,
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
        this.movesPossible = new HashSet<>();

        switch (this.type) {
            case KING -> this.movesKing(board, myPosition);
            case QUEEN -> this.movesQueen(board, myPosition);
            case BISHOP -> this.movesBishop(board, myPosition);
            case KNIGHT -> this.movesQueen(board, myPosition);
            case ROOK -> this.movesQueen(board, myPosition);
            case PAWN -> this.movesQueen(board, myPosition);
            default ->
                    throw new RuntimeException("Used Wrong PieceType! PieceType Entered: " + this.type + " Expected Enum of ChessPiece.PieceType");
        }

        return this.movesPossible;
    }

    private void movesKing(ChessBoard board, ChessPosition myPosition) {
//        return Collection(new ChessMove(new ChessPosition(1,2), new ChessPosition(4, 5), this.type);
        search(board, myPosition, Direction.UP, 1);
        search(board, myPosition, Direction.DOWN, 1);
        search(board, myPosition, Direction.LEFT, 1);
        search(board, myPosition, Direction.RIGHT, 1);
        search(board, myPosition, Direction.UPLEFT, 1);
        search(board, myPosition, Direction.UPRIGHT, 1);
        search(board, myPosition, Direction.DOWNLEFT, 1);
        search(board, myPosition, Direction.DOWNRIGHT, 1);

    }

    private void movesQueen(ChessBoard board, ChessPosition myPosition) {
        search(board, myPosition, Direction.UP, 10);
        search(board, myPosition, Direction.DOWN, 10);
        search(board, myPosition, Direction.LEFT, 10);
        search(board, myPosition, Direction.RIGHT, 10);
        search(board, myPosition, Direction.UPLEFT, 10);
        search(board, myPosition, Direction.UPRIGHT, 10);
        search(board, myPosition, Direction.DOWNLEFT, 10);
        search(board, myPosition, Direction.DOWNRIGHT, 10);

    }

    private void movesBishop(ChessBoard board, ChessPosition myPosition) {
        search(board, myPosition, Direction.UPLEFT, 10);
        search(board, myPosition, Direction.UPRIGHT, 10);
        search(board, myPosition, Direction.DOWNLEFT, 10);
        search(board, myPosition, Direction.DOWNRIGHT, 10);

    }

//    private Collection<ChessMove> movesBishop(ChessBoard board, ChessPosition myPosition) {
//        return true;
//    }
//
//    private Collection<ChessMove> movesKnight() {
//        return true;
//    }
//
//    private Collection<ChessMove> movesRook() {
//        return true;
//    }
//
//    private Collection<ChessMove> movesPawn() {
//        return true;
//    }

    private void search(ChessBoard board, ChessPosition myPosition, ChessPiece.Direction direction, int numSearch) {

        ChessPosition newPos = null;

        for (int i = 1; i < numSearch + 1; i++) {

            switch (direction) {
                case UP -> newPos = new ChessPosition(myPosition.getRow() + i + 1, myPosition.getColumn() + 1);
                case DOWN -> newPos = new ChessPosition(myPosition.getRow() - i + 1, myPosition.getColumn() + 1);
                case LEFT -> newPos = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - i + 1);
                case RIGHT -> newPos = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + i + 1);
                case UPLEFT -> newPos = new ChessPosition(myPosition.getRow() + i + 1, myPosition.getColumn() - i + 1);
                case UPRIGHT -> newPos = new ChessPosition(myPosition.getRow() + i + 1, myPosition.getColumn() + i + 1);
                case DOWNLEFT ->
                        newPos = new ChessPosition(myPosition.getRow() - i + 1, myPosition.getColumn() - i + 1);
                case DOWNRIGHT ->
                        newPos = new ChessPosition(myPosition.getRow() - i + 1, myPosition.getColumn() + i + 1);
                default ->
                        throw new RuntimeException("Used Wrong Direction! Direction Entered: " + direction + " Expected Enum of ChessPiece.Direction");
            }

            // Catch out of bounds queries
            if (
                    newPos.getRow() >= 0 &&
                            newPos.getColumn() >= 0 &&
                            newPos.getRow() < 8 &&
                            newPos.getColumn() < 8
            ) {
                // Catch piece collisions
                if (board.getPiece(newPos) == null) {
                    this.movesPossible.add(new ChessMove(myPosition, newPos, this.type));
                }
            }
        }

    }

    @Override
    public String toString() {
        return "" + this.pieceColor.toString().substring(0, 1) + " " + this.type.toString().substring(0, 2) + "";
    }
}
