package chess;

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
//        throw new RuntimeException("Not implemented");

        return this.row;

    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {

//        throw new RuntimeException("Not implemented");

        return this.col;

    }

    @Override
    public String toString() {
        return "(" + (this.col + 1) + ", " + (this.row + 1) + ")";
    }
}
