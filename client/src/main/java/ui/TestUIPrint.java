package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static client.EscapeSequences.*;

public class TestUIPrint {

    // Board dimensions.
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 3;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 1;

    // Piece arrays for easy board setup.
    private static final String[] WHITE_PIECES = {
            WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK
    };
    private static final String[] BLACK_PIECES = {
            BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK
    };

    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print("\u001b[2J"); // Clear the screen.
        drawChessBoard(out);
    }

    private static void drawChessBoard(PrintStream out) {
        for (int row = 0; row < BOARD_SIZE_IN_SQUARES; ++row) {
            drawRowOfSquares(out, row);
        }
    }

    private static void drawRowOfSquares(PrintStream out, int row) {
        for (int squareRow = 0; squareRow < (SQUARE_SIZE_IN_PADDED_CHARS); ++squareRow) {
            for (int col = 0; col < BOARD_SIZE_IN_SQUARES; ++col) {
                if ((row + col) % 2 == 0) {
                    setWhite(out); // White square
                } else {
                    setDarkGrey(out); // Black square
                }

                if (squareRow == SQUARE_SIZE_IN_PADDED_CHARS / 2) {
                    int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
                    int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;

                    out.print(EMPTY.repeat(prefixLength));

                    if (row == 1) {
                        out.print(WHITE_PAWN); // White pawns row
                    } else if (row == 6) {
                        out.print(BLACK_PAWN); // Black pawns row
                    } else if (row == 0) {
                        out.print(WHITE_PIECES[col]); // White back row pieces
                    } else if (row == 7) {
                        out.print(BLACK_PIECES[col]); // Black back row pieces
                    } else {
                        out.print(EMPTY); // Empty spaces for other squares
                    }

                    out.print(EMPTY.repeat(suffixLength));
                } else {
                    out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
                }
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
        out.print(SET_BG_COLOR_WHITE); // Set background to white
        out.print(SET_TEXT_COLOR_BLACK); // Set text to black
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK); // Set background to black
        out.print(SET_TEXT_COLOR_WHITE); // Set text to white
    }

    private static void setDarkGrey(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREY); // Set background to dark gray
        out.print(SET_TEXT_COLOR_BLACK); // Set text to white
    }
}
