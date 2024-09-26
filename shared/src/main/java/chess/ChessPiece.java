package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;
    private Collection<ChessMove> movesPossible = new HashSet<>();

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
        KLEFTUP,
        KUPLEFT,
        KUPRIGHT,
        KRIGHTUP,
        KRIGHTDOWN,
        KDOWNRIGHT,
        KDOWNLEFT,
        KLEFTDOWN
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
            case KNIGHT -> this.movesKnight(board, myPosition);
            case ROOK -> this.movesRook(board, myPosition);
            case PAWN -> this.movesPawn(board, myPosition);
            default ->
                    throw new RuntimeException("Used Wrong PieceType! PieceType Entered: " + this.type + " Expected Enum of ChessPiece.PieceType");
        }

        return this.movesPossible;
    }

    private void movesKing(ChessBoard board, ChessPosition myPosition) {
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

    private void movesKnight(ChessBoard board, ChessPosition myPosition) {
        search(board, myPosition, Direction.KLEFTUP, 1);
        search(board, myPosition, Direction.KUPLEFT, 1);
        search(board, myPosition, Direction.KUPRIGHT, 1);
        search(board, myPosition, Direction.KRIGHTUP, 1);
        search(board, myPosition, Direction.KRIGHTDOWN, 1);
        search(board, myPosition, Direction.KDOWNRIGHT, 1);
        search(board, myPosition, Direction.KDOWNLEFT, 1);
        search(board, myPosition, Direction.KLEFTDOWN, 1);
    }

    private void movesRook(ChessBoard board, ChessPosition myPosition) {
        search(board, myPosition, Direction.UP, 10);
        search(board, myPosition, Direction.DOWN, 10);
        search(board, myPosition, Direction.LEFT, 10);
        search(board, myPosition, Direction.RIGHT, 10);
    }

    private void movesPawn(ChessBoard board, ChessPosition myPosition) {
        if (this.getTeamColor() == ChessGame.TeamColor.WHITE) {
            if (myPosition.getNiceRow() == 2) {
                search(board, myPosition, Direction.UP, 2);
            } else {
                search(board, myPosition, Direction.UP, 1);
            }
            search(board, myPosition, Direction.UPLEFT, 1);
            search(board, myPosition, Direction.UPRIGHT, 1);
        } else {
            if (myPosition.getNiceRow() == 7) {
                search(board, myPosition, Direction.DOWN, 2);
            } else {
                search(board, myPosition, Direction.DOWN, 1);
            }
            search(board, myPosition, Direction.DOWNLEFT, 1);
            search(board, myPosition, Direction.DOWNRIGHT, 1);
        }
    }

    private void search(ChessBoard board, ChessPosition myPosition, ChessPiece.Direction direction, int numSearch) {
        for (int i = 1; i < numSearch + 1; i++) {
            ChessPosition newPos = handleOffSet(myPosition, direction, i);
            boolean flagPromotion = false;

            // Catch out of bounds queries
            if (
                    newPos.getRow() >= 0 &&
                            newPos.getColumn() >= 0 &&
                            newPos.getRow() < 8 &&
                            newPos.getColumn() < 8
            ) {
                if (this.getPieceType() == PieceType.PAWN) {
                    if (this.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        if (newPos.getNiceRow() == 8) {
                            flagPromotion = true;
                        }
                    } else {
                        if (newPos.getNiceRow() == 1) {
                            flagPromotion = true;
                        }
                    }
                }

                // If nothing is there, then allow to move there
                if (board.getPiece(newPos) == null) {

                    // If the pawn is moving diagonally, we only want to record if it is capturing
                    if (this.getPieceType() == PieceType.PAWN) {
                        if (direction == Direction.UP || direction == Direction.DOWN) {
                            if (flagPromotion) {
                                for (int j = 0; j < 4; j++) {
                                    this.movesPossible.add(new ChessMove(myPosition, newPos, PieceType.QUEEN));
                                    this.movesPossible.add(new ChessMove(myPosition, newPos, PieceType.BISHOP));
                                    this.movesPossible.add(new ChessMove(myPosition, newPos, PieceType.KNIGHT));
                                    this.movesPossible.add(new ChessMove(myPosition, newPos, PieceType.ROOK));
                                }
                            } else {
                                this.movesPossible.add(new ChessMove(myPosition, newPos, null)); //Null for now until pawns
                            }
                        }
                    } else {
                        this.movesPossible.add(new ChessMove(myPosition, newPos, null)); //Null for now until pawns
                    }


                } else {

                    // Catch piece collisions with the opposing team
                    if (board.getPiece(newPos).getTeamColor() != this.getTeamColor()) {
                        if (this.getPieceType() == PieceType.PAWN) {
                            if (direction != Direction.UP && direction != Direction.DOWN) {
                                if (flagPromotion) {
                                    for (int j = 0; j < 4; j++) {
                                        this.movesPossible.add(new ChessMove(myPosition, newPos, PieceType.QUEEN));
                                        this.movesPossible.add(new ChessMove(myPosition, newPos, PieceType.BISHOP));
                                        this.movesPossible.add(new ChessMove(myPosition, newPos, PieceType.KNIGHT));
                                        this.movesPossible.add(new ChessMove(myPosition, newPos, PieceType.ROOK));
                                    }
                                } else {
                                    this.movesPossible.add(new ChessMove(myPosition, newPos, null)); //Null for now until pawns
                                }
                            }
                        } else {
                            this.movesPossible.add(new ChessMove(myPosition, newPos, null));
                        }
                    }
                    return;
                }
            } else {
                return; // If it is out of bounds, it should stop checking
            }
        }
    }

    private ChessPosition handleOffSet(ChessPosition myPosition, ChessPiece.Direction direction, int i) {
        ChessPosition newPos;
        switch (direction) {
            case UP -> newPos = new ChessPosition(myPosition.getNiceRow() + i, myPosition.getNiceColumn());
            case DOWN -> newPos = new ChessPosition(myPosition.getNiceRow() - i, myPosition.getNiceColumn());
            case LEFT -> newPos = new ChessPosition(myPosition.getNiceRow(), myPosition.getNiceColumn() - i);
            case RIGHT -> newPos = new ChessPosition(myPosition.getNiceRow(), myPosition.getNiceColumn() + i);
            case UPLEFT -> newPos = new ChessPosition(myPosition.getNiceRow() + i, myPosition.getNiceColumn() - i);
            case UPRIGHT -> newPos = new ChessPosition(myPosition.getNiceRow() + i, myPosition.getNiceColumn() + i);
            case DOWNLEFT -> newPos = new ChessPosition(myPosition.getNiceRow() - i, myPosition.getNiceColumn() - i);
            case DOWNRIGHT -> newPos = new ChessPosition(myPosition.getNiceRow() - i, myPosition.getNiceColumn() + i);
            case KLEFTUP -> newPos = new ChessPosition(myPosition.getNiceRow() + 1, myPosition.getNiceColumn() - 2);
            case KUPLEFT -> newPos = new ChessPosition(myPosition.getNiceRow() + 2, myPosition.getNiceColumn() - 1);
            case KUPRIGHT -> newPos = new ChessPosition(myPosition.getNiceRow() + 2, myPosition.getNiceColumn() + 1);
            case KRIGHTUP -> newPos = new ChessPosition(myPosition.getNiceRow() + 1, myPosition.getNiceColumn() + 2);
            case KRIGHTDOWN -> newPos = new ChessPosition(myPosition.getNiceRow() - 1, myPosition.getNiceColumn() + 2);
            case KDOWNRIGHT -> newPos = new ChessPosition(myPosition.getNiceRow() - 2, myPosition.getNiceColumn() + 1);
            case KDOWNLEFT -> newPos = new ChessPosition(myPosition.getNiceRow() - 2, myPosition.getNiceColumn() - 1);
            case KLEFTDOWN -> newPos = new ChessPosition(myPosition.getNiceRow() - 1, myPosition.getNiceColumn() - 2);
            default ->
                    throw new RuntimeException("Used Wrong Direction! Direction Entered: " + direction + " Expected Enum of ChessPiece.Direction");
        }
        return newPos;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "" + this.pieceColor.toString().substring(0, 1) + " " + this.type.toString().substring(0, 2) + "";
    }
}
