package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int row;
    private final int col;

    public ChessPosition(int row, int col) {

        this.row = row - 1;
        this.col = col - 1;

    }

    public ChessPosition(ChessPosition.Column colChar, int row) {

        this.row = row - 1;

        switch (colChar) {
            case A -> this.col = 0;
            case B -> this.col = 1;
            case C -> this.col = 2;
            case D -> this.col = 3;
            case E -> this.col = 4;
            case F -> this.col = 5;
            case G -> this.col = 6;
            case H -> this.col = 7;
            default ->
                    throw new RuntimeException("Used Wrong Column! Column Entered: " + colChar + " Expected Enum of ChessPosition.Column");
        }

    }

    public enum Column {
        A, B, C, D, E, F, G, H
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return this.row;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getNiceRow() {
        return this.row + 1;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return this.col;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getNiceColumn() {
        return this.col + 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPosition that = (ChessPosition) o;
        return getRow() == that.getRow() && getColumn() == that.getColumn();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRow(), getColumn());
    }

    @Override
    public String toString() {
        return "(" + (this.col + 1) + ", " + (this.row + 1) + ")";
    }
}
