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

    // Copy constructor
    public ChessPiece(ChessPiece other) {
        this.pieceColor = other.pieceColor;
        this.type = other.type;
        this.movesPossible = new HashSet<>(other.movesPossible);
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

    private void search(ChessBoard board, ChessPosition currPos, ChessPiece.Direction direction, int numSearch) {
        for (int i = 1; i < numSearch + 1; i++) {
            ChessPosition newPos = handleOffSet(currPos, direction, i);
            boolean flagPromotion = false;

            ChessMove newMove = new ChessMove(currPos, newPos, null);

            if (this.getPieceType() != PieceType.PAWN) {


                if ( // Make sure is in bounds
                        newPos.getNiceRow() > 0 &&
                                newPos.getNiceRow() <= 8 &&
                                newPos.getNiceColumn() > 0 &&
                                newPos.getNiceColumn() <= 8
                ) {

                    if (board.getPiece(newPos) == null) {
                        this.movesPossible.add(newMove);
                    } else if (board.getPiece(newPos).getTeamColor() != this.getTeamColor()) { // Is enemy
                        this.movesPossible.add(newMove);
                        return;
                    } else {
                        return; // Can't phase through your own pieces
                    }

                } else {
                    return;
                }

            } else {
                i = handlePawnSearch(board, direction, currPos, newPos, newMove, i);
            }
        }
    }


    private ChessPosition handleOffSet(ChessPosition currPos, ChessPiece.Direction direction, int i) {
        ChessPosition newPos;
        switch (direction) {
            case UP -> newPos = new ChessPosition(currPos.getNiceRow() + i, currPos.getNiceColumn());
            case DOWN -> newPos = new ChessPosition(currPos.getNiceRow() - i, currPos.getNiceColumn());
            case LEFT -> newPos = new ChessPosition(currPos.getNiceRow(), currPos.getNiceColumn() - i);
            case RIGHT -> newPos = new ChessPosition(currPos.getNiceRow(), currPos.getNiceColumn() + i);
            case UPLEFT -> newPos = new ChessPosition(currPos.getNiceRow() + i, currPos.getNiceColumn() - i);
            case UPRIGHT -> newPos = new ChessPosition(currPos.getNiceRow() + i, currPos.getNiceColumn() + i);
            case DOWNLEFT -> newPos = new ChessPosition(currPos.getNiceRow() - i, currPos.getNiceColumn() - i);
            case DOWNRIGHT -> newPos = new ChessPosition(currPos.getNiceRow() - i, currPos.getNiceColumn() + i);
            case KLEFTUP -> newPos = new ChessPosition(currPos.getNiceRow() + 1, currPos.getNiceColumn() - 2);
            case KUPLEFT -> newPos = new ChessPosition(currPos.getNiceRow() + 2, currPos.getNiceColumn() - 1);
            case KUPRIGHT -> newPos = new ChessPosition(currPos.getNiceRow() + 2, currPos.getNiceColumn() + 1);
            case KRIGHTUP -> newPos = new ChessPosition(currPos.getNiceRow() + 1, currPos.getNiceColumn() + 2);
            case KRIGHTDOWN -> newPos = new ChessPosition(currPos.getNiceRow() - 1, currPos.getNiceColumn() + 2);
            case KDOWNRIGHT -> newPos = new ChessPosition(currPos.getNiceRow() - 2, currPos.getNiceColumn() + 1);
            case KDOWNLEFT -> newPos = new ChessPosition(currPos.getNiceRow() - 2, currPos.getNiceColumn() - 1);
            case KLEFTDOWN -> newPos = new ChessPosition(currPos.getNiceRow() - 1, currPos.getNiceColumn() - 2);
            default ->
                    throw new RuntimeException("Used Wrong Direction! Direction Entered: " + direction + " Expected Enum of ChessPiece.Direction");
        }
        return newPos;

    }

    private int handlePawnSearch(
            ChessBoard board,
            ChessPiece.Direction direction,
            ChessPosition currPos,
            ChessPosition newPos,
            ChessMove newMove,
            int currentMoveNumber
    ) {

        // Wierd Pawn Stuff
        boolean promotionFlag = false;

        if (this.getTeamColor() == ChessGame.TeamColor.WHITE) {
            if (newPos.getNiceRow() == 8) {
                promotionFlag = true;
            }
        } else {
            if (newPos.getNiceRow() == 1) {
                promotionFlag = true;
            }
        }

        if ( // Make sure is in bounds
                newPos.getNiceRow() > 0 &&
                        newPos.getNiceRow() <= 8 &&
                        newPos.getNiceColumn() > 0 &&
                        newPos.getNiceColumn() <= 8
        ) {
            if (this.getTeamColor() == ChessGame.TeamColor.WHITE) {
                if (direction == Direction.UP) {
                    currentMoveNumber = testPawnStraightMove(board, newMove, currPos, newPos, promotionFlag, currentMoveNumber);
                } else { // Going Diagonal
                    testPawnDiagonalMove(board, newMove, currPos, newPos, promotionFlag);
                }
            } else { // Is Black
                if (direction == Direction.DOWN) {
                    currentMoveNumber = testPawnStraightMove(board, newMove, currPos, newPos, promotionFlag, currentMoveNumber);
                } else { // Going Diagonal
                    testPawnDiagonalMove(board, newMove, currPos, newPos, promotionFlag);
                }
            }
        }
        return currentMoveNumber;
    }

    private int testPawnStraightMove(
            ChessBoard board,
            ChessMove newMove,
            ChessPosition currPos,
            ChessPosition newPos,
            boolean promotionFlag,
            int currentMoveNumber
    ) {
        if (board.getPiece(newPos) == null && true) {
            if (promotionFlag && true) {
                this.movesPossible.add(new ChessMove(currPos, newPos, PieceType.QUEEN));
                this.movesPossible.add(new ChessMove(currPos, newPos, PieceType.BISHOP));
                this.movesPossible.add(new ChessMove(currPos, newPos, PieceType.KNIGHT));
                this.movesPossible.add(new ChessMove(currPos, newPos, PieceType.ROOK));
            } else {
                this.movesPossible.add(newMove);
            }
        } else {
            return 10; // If any piece ahead, no further moves
        }
        return currentMoveNumber;
    }

    private void testPawnDiagonalMove(
            ChessBoard board,
            ChessMove newMove,
            ChessPosition currPos,
            ChessPosition newPos,
            boolean promotionFlag
    ) {
        if (board.getPiece(newPos) != null) {
            if (board.getPiece(newPos).getTeamColor() != this.getTeamColor()) {
                if (promotionFlag) {
                    this.movesPossible.add(new ChessMove(currPos, newPos, PieceType.QUEEN));
                    this.movesPossible.add(new ChessMove(currPos, newPos, PieceType.BISHOP));
                    this.movesPossible.add(new ChessMove(currPos, newPos, PieceType.KNIGHT));
                    this.movesPossible.add(new ChessMove(currPos, newPos, PieceType.ROOK));
                } else {
                    this.movesPossible.add(newMove);
                }
            }
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
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
