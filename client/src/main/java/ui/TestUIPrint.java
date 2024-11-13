package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class TestUIPrint {

    // Board dimensions.
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 3;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 1;

    // Padded characters.
    private static final String EMPTY = "   ";
    private static final String WHITE_SQUARE = " W ";
    private static final String BLACK_SQUARE = " B ";

    // Chess pieces, represented by letters
    private static final String[] WHITE_PIECES = {
            "R", "N", "B", "Q", "K", "B", "N", "R" // White pieces: Rook, Knight, Bishop, Queen, King, Bishop, Knight, Rook
    };
    private static final String[] BLACK_PIECES = {
            "r", "n", "b", "q", "k", "b", "n", "r" // Black pieces: rook, knight, bishop, queen, king, bishop, knight, rook
    };
    private static final String[] EMPTY_ROW = {"   ", "   ", "   ", "   ", "   ", "   ", "   ", "   "}; // Empty row for pawns.

    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print("\u001b[2J"); // Clear the screen.

        drawChessBoard(out);
    }

    private static void drawChessBoard(PrintStream out) {
        for (int row = 0; row < BOARD_SIZE_IN_SQUARES; ++row) {
            drawRowOfSquares(out, row);

//            if (row < BOARD_SIZE_IN_SQUARES - 1) {
//                // Draw horizontal separator between rows.
//                drawHorizontalLine(out);
//            }
        }
    }

    private static void drawRowOfSquares(PrintStream out, int row) {
        for (int squareRow = 0; squareRow < SQUARE_SIZE_IN_PADDED_CHARS; ++squareRow) {
            for (int col = 0; col < BOARD_SIZE_IN_SQUARES; ++col) {
                if ((row + col) % 2 == 0) {
                    setWhite(out); // White square
                } else {
                    setBlack(out); // Black square
                }

                if (squareRow == SQUARE_SIZE_IN_PADDED_CHARS / 2) {
                    int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
                    int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;

                    out.print(EMPTY.repeat(prefixLength));

                    if (row == 1) {
                        out.print(WHITE_PIECES[col]); // White pawns
                    } else if (row == 6) {
                        out.print(BLACK_PIECES[col]); // Black pawns
                    } else {
                        if (row == 0) {
                            out.print(WHITE_PIECES[col]); // White back row pieces
                        } else if (row == 7) {
                            out.print(BLACK_PIECES[col]); // Black back row pieces
                        } else {
                            out.print("   "); // Empty spaces for the rest of the board
                        }
                    }

                    out.print(EMPTY.repeat(suffixLength));
                } else {
                    out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
                }

//                if (col < BOARD_SIZE_IN_SQUARES - 1) {
//                    out.print(EMPTY.repeat(LINE_WIDTH_IN_PADDED_CHARS)); // Column separator
//                }
            }
            setBlack(out);
            out.println();
        }
    }

    private static void drawHorizontalLine(PrintStream out) {
        int boardSizeInSpaces = BOARD_SIZE_IN_SQUARES * SQUARE_SIZE_IN_PADDED_CHARS +
                (BOARD_SIZE_IN_SQUARES - 1) * LINE_WIDTH_IN_PADDED_CHARS;

        for (int lineRow = 0; lineRow < LINE_WIDTH_IN_PADDED_CHARS; ++lineRow) {
            out.print(EMPTY.repeat(boardSizeInSpaces));
            out.println();
        }
    }

    private static void setWhite(PrintStream out) {
        out.print("\u001b[47m"); // Set background to white
        out.print("\u001b[30m"); // Set text to black
    }

    private static void setBlack(PrintStream out) {
        out.print("\u001b[40m"); // Set background to black
        out.print("\u001b[37m"); // Set text to white
    }
}
