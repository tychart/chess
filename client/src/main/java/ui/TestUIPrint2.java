package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static client.EscapeSequences.*;


public class TestUIPrint2 {

    // Board dimensions (8x8)
    private static final int BOARD_SIZE = 8;
    private static final int SQUARE_SIZE = 3;

    // Characters for empty squares and pieces (white uppercase, black lowercase)
    private static final String EMPTY = "   ";
    private static final String WHITE_KING = " K ";
    private static final String WHITE_QUEEN = " Q ";
    private static final String WHITE_ROOK = " R ";
    private static final String WHITE_BISHOP = " B ";
    private static final String WHITE_KNIGHT = " N ";
    private static final String WHITE_PAWN = " P ";
    private static final String BLACK_KING = " k ";
    private static final String BLACK_QUEEN = " q ";
    private static final String BLACK_ROOK = " r ";
    private static final String BLACK_BISHOP = " b ";
    private static final String BLACK_KNIGHT = " n ";
    private static final String BLACK_PAWN = " p ";

    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        drawChessBoard(out);

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawChessBoard(PrintStream out) {
        // Loop through each row
        for (int row = 0; row < BOARD_SIZE; row++) {
            // Alternate background color for each row
            out.print(row % 2 == 0 ? SET_BG_COLOR_WHITE : SET_BG_COLOR_BLACK);

            // Loop through each square
            for (int col = 0; col < BOARD_SIZE; col++) {
                drawSquare(out, row, col);

                // Add separator between squares
                if (col < BOARD_SIZE - 1) {
                    out.print(SET_BG_COLOR_BLACK);
                    out.print(EMPTY.repeat(SQUARE_SIZE - 1));
                }
            }

            out.println(); // Move to next line after each row
        }
    }

    private static void drawSquare(PrintStream out, int row, int col) {
        // Alternate foreground color for white and black squares
        out.print(isLightSquare(row, col) ? SET_TEXT_COLOR_BLACK : SET_TEXT_COLOR_WHITE);

        // Print empty square or piece (replace with your piece logic)
        out.print(EMPTY);

        out.print(SET_BG_COLOR_BLACK); // Reset background color
    }

    private static boolean isLightSquare(int row, int col) {
        return (row + col) % 2 == 0; // Light squares on sum of row and col even
    }
}