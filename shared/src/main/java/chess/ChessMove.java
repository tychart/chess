package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition, ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return this.startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return this.endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return this.promotionPiece;
    }

//    @Override
//    public String toString() {
//        return "[" + this.startPosition + " -> " + this.endPosition + "]";
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(getStartPosition(), chessMove.getStartPosition()) &&
                Objects.equals(getEndPosition(), chessMove.getEndPosition()) &&
                getPromotionPiece() == chessMove.getPromotionPiece();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStartPosition(), getEndPosition(), getPromotionPiece());
    }

    @Override
    public String toString() {
        StringBuilder outstr = new StringBuilder("ChessMove[\n8 ");

        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                if (i == this.startPosition.getRow() && j == this.startPosition.getColumn()) {
                    outstr.append(" S ");
                } else if (i == this.endPosition.getRow() && j == this.endPosition.getColumn()) {
                    outstr.append(" E ");
                } else {
                    outstr.append(" * ");
                }
            }
            outstr.append("\n" + i + " ");
        }
        outstr.append("\n");
        return outstr.toString();
    }
}
