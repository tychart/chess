package ui;

import chess.*;


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

    ChessGame chessGame;
    ChessBoard chessBoard;


    public TestUIPrint(ChessGame chessGame) {
        this.chessGame = chessGame;

    }

    public void drawNormalChessBoard() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print("\u001b[2J"); // Clear the screen.
        this.chessBoard = chessGame.getBoard();
        System.out.println(chessBoard.getPiece(new ChessPosition(1, 1)));
        System.out.println(chessBoard);
        drawNormal(out);
    }

    public void drawFlippedChessBoard() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print("\u001b[2J"); // Clear the screen.
        this.chessBoard = chessGame.getBoard();
        System.out.println(chessBoard.getPiece(new ChessPosition(1, 1)));
        System.out.println(chessBoard);
        drawFlipped(out);
    }

    private void drawNormal(PrintStream out) {
        for (int row = BOARD_SIZE_IN_SQUARES - 1; row >= 0; row--) {
            drawRowOfSquares(out, row);
        }
    }

    private void drawFlipped(PrintStream out) {
        for (int row = 0; row < BOARD_SIZE_IN_SQUARES; ++row) {
            drawRowOfSquares(out, row);
        }
    }

    private void drawRowOfSquares(PrintStream out, int row) {
        for (int squareRow = 0; squareRow < (SQUARE_SIZE_IN_PADDED_CHARS); ++squareRow) {
            for (int col = 0; col < BOARD_SIZE_IN_SQUARES; ++col) {
                if ((row + col) % 2 == 0) {
//                    setWhite(out); // White square
                    setDarkGrey(out);
                } else {
//                    setDarkGrey(out); // Black square
                    setWhite(out);
                }

                if (squareRow == SQUARE_SIZE_IN_PADDED_CHARS / 2) {
                    int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
                    int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;

                    out.print(EMPTY.repeat(prefixLength));

                    out.print(this.getPiecePretty(row + 1, col + 1));

                    out.print(EMPTY.repeat(suffixLength));
                } else {
                    out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
                }
            }
            setBlack(out);
            out.println();
        }
    }

    private String getPiecePretty(int row, int col) {
        ChessPiece currPiece = chessBoard.getPiece(new ChessPosition(row, col));
        if (currPiece == null) {
            return EMPTY;
        }
        return prettyPiece(currPiece);
    }

    private String prettyPiece(ChessPiece chessPiece) {
        return switch (chessPiece.getTeamColor()) {
            case WHITE -> switch (chessPiece.getPieceType()) {
                case KING -> WHITE_KING;
                case QUEEN -> WHITE_QUEEN;
                case BISHOP -> WHITE_BISHOP;
                case KNIGHT -> WHITE_KNIGHT;
                case ROOK -> WHITE_ROOK;
                case PAWN -> WHITE_PAWN;
            };
            case BLACK -> switch (chessPiece.getPieceType()) {
                case KING -> BLACK_KING;
                case QUEEN -> BLACK_QUEEN;
                case BISHOP -> BLACK_BISHOP;
                case KNIGHT -> BLACK_KNIGHT;
                case ROOK -> BLACK_ROOK;
                case PAWN -> BLACK_PAWN;
            };
        };
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
