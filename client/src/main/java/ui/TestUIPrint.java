package ui;

import chess.*;


import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;


import static client.EscapeSequences.*;

public class TestUIPrint {

    // Board dimensions.
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 3;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 1;

    // Piece arrays for easy board setup.
    private static final String[] ROW_LABELS = {"1", "2", "3", "4", "5", "6", "7", "8"};
    private static final String[] COL_LABELS = {" a ", " b ", " c ", " d ", " e ", " f ", " g ", " h "};


    ChessGame chessGame;
    ChessBoard chessBoard;


    public TestUIPrint(ChessGame chessGame) {
        this.chessGame = chessGame;

    }

    public void drawNormalChessBoard(Collection<ChessMove> highlightMoves) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print("\u001b[2J"); // Clear the screen.
        this.chessBoard = chessGame.getBoard();
        drawNormal(out, highlightMoves);
    }

    public void drawFlippedChessBoard(Collection<ChessMove> highlightMoves) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print("\u001b[2J"); // Clear the screen.
        this.chessBoard = chessGame.getBoard();
        drawFlipped(out, highlightMoves);
    }

    private void drawNormal(PrintStream out, Collection<ChessMove> highlightMoves) {
        drawTopBoarder(out, false);

        for (int row = BOARD_SIZE_IN_SQUARES - 1; row >= 0; row--) {
            drawRowOfSquares(out, row, false, highlightMoves);
        }

        drawBottomBoarder(out, false);
    }

    private void drawFlipped(PrintStream out, Collection<ChessMove> highlightMoves) {
        drawTopBoarder(out, true);

        for (int row = 0; row < BOARD_SIZE_IN_SQUARES; ++row) {
            drawRowOfSquares(out, row, true, highlightMoves);
        }

        drawBottomBoarder(out, true);
    }

    private void drawRowOfSquares(
            PrintStream out, int row, boolean flipped, Collection<ChessMove> highlightMoves) {
        for (int squareRow = 0; squareRow < SQUARE_SIZE_IN_PADDED_CHARS; ++squareRow) {
            drawLeftBorder(out, row, squareRow);

            for (int col = 0; col < BOARD_SIZE_IN_SQUARES; ++col) {
                ChessPosition chessPos;
                if (!flipped) {
                    chessPos = new ChessPosition(row + 1, col + 1);
                } else {
                    chessPos = new ChessPosition(row + 1, BOARD_SIZE_IN_SQUARES - col);
                }

                boolean isHighlighted = isPositionHighlighted(chessPos, highlightMoves);

                // Determine the square color based on row, column, and flipped state
                boolean isDarkSquare = (row + col) % 2 == 0;


                if (isHighlighted) {
                    setHighlightColor(out);
                } else {
                    setSquareColor(out, isDarkSquare, flipped);
                }

                // Check if this is the middle row of the square to print a piece
                if (squareRow == SQUARE_SIZE_IN_PADDED_CHARS / 2) {
                    drawSquareContent(out, row, col, flipped);
                } else {
                    out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
                }
            }

            drawRightBorder(out, row, squareRow);
            setBlack(out); // Reset color after drawing each row
            out.println();
        }
    }

    /**
     * Checks if a given position is highlighted based on the collection of ChessMoves.
     */
    private boolean isPositionHighlighted(ChessPosition position, Collection<ChessMove> highlightMoves) {
        if (highlightMoves.isEmpty()) {
            return false;
        }
        for (ChessMove move : highlightMoves) {
            if (move.getEndPosition().equals(position)) {
                return true; // Found a match
            }
        }
        return false; // No match found
    }

    /**
     * Sets the color of the square based on whether it's dark or light and the flipped state.
     */
    private void setSquareColor(PrintStream out, boolean isDarkSquare, boolean flipped) {
        if ((isDarkSquare && !flipped) || (!isDarkSquare && flipped)) {
            setDarkGrey(out);
        } else {
            setWhite(out);
        }
    }

    private void setHighlightColor(PrintStream out) {
        setHighlight(out);
    }

    /**
     * Draws the content of the square for the middle row. Handles flipped state.
     */
    private void drawSquareContent(PrintStream out, int row, int col, boolean flipped) {
        int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
        int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;

        out.print(EMPTY.repeat(prefixLength));

        // Retrieve and print the piece for the current square
        if (!flipped) {
            out.print(getPiecePretty(row + 1, col + 1));
        } else {
            out.print(getPiecePretty(row + 1, BOARD_SIZE_IN_SQUARES - col));
        }

        out.print(EMPTY.repeat(suffixLength));
    }


    private static void drawTopBoarder(PrintStream out, boolean flipped) {
        drawHorizontalLabels(out, flipped);
        drawHorizontalLine(out);
    }

    private static void drawLeftBorder(PrintStream out, int row, int squareRow) {

        drawVerticalLabels(out, row, squareRow);
        drawVerticalLine(out);

    }

    private static void drawBottomBoarder(PrintStream out, boolean flipped) {
        drawHorizontalLine(out);
        drawHorizontalLabels(out, flipped);
    }

    private static void drawRightBorder(PrintStream out, int row, int squareRow) {

        drawVerticalLine(out);
        drawVerticalLabels(out, row, squareRow);


    }

    private static void drawHorizontalLabels(PrintStream out, boolean flipped) {
        int boardLenInPaddedChars = BOARD_SIZE_IN_SQUARES * SQUARE_SIZE_IN_PADDED_CHARS;
        int cornerSize = SQUARE_SIZE_IN_PADDED_CHARS + 1;
        List<String> orientatedCols = new ArrayList<>();
        Collections.addAll(orientatedCols, COL_LABELS);


        if (flipped) {
            Collections.reverse(orientatedCols);
        }

        setWhite(out);
        out.print(EMPTY.repeat(boardLenInPaddedChars + cornerSize * 2));

        setBackgroud(out);
        out.println();

        int prefix = SQUARE_SIZE_IN_PADDED_CHARS / 2;
        int suffix = SQUARE_SIZE_IN_PADDED_CHARS / 2;

        setWhite(out);
        out.print(EMPTY.repeat(cornerSize));
        for (int i = 0; i < BOARD_SIZE_IN_SQUARES; i++) {
            out.print(EMPTY.repeat(prefix));
            out.print(orientatedCols.get(i));
            out.print(EMPTY.repeat(suffix));
        }
        out.print(EMPTY.repeat(cornerSize));

        setBackgroud(out);
        out.println();

        setWhite(out);
        out.print(EMPTY.repeat(boardLenInPaddedChars + cornerSize * 2));
        setBackgroud(out);
        out.println();

    }

    private static void drawHorizontalLine(PrintStream out) {

        int boardLenInPaddedChars = BOARD_SIZE_IN_SQUARES * SQUARE_SIZE_IN_PADDED_CHARS;

        for (int lineRow = 0; lineRow < LINE_WIDTH_IN_PADDED_CHARS; ++lineRow) {
            setWhite(out);
            out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));

            setBlack(out);
            out.print(EMPTY.repeat(boardLenInPaddedChars + 2));

            setWhite(out);
            out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
            setBackgroud(out);
            out.println();
        }
    }

    private static void drawVerticalLabels(PrintStream out, int row, int squareRow) {
        setWhite(out);

        if (squareRow == SQUARE_SIZE_IN_PADDED_CHARS / 2) {
            out.print(EMPTY);
            out.print(" ");
            out.print(ROW_LABELS[row]);
            out.print(" ");
            out.print(EMPTY);
        } else {
            out.print(EMPTY.repeat(3));
        }
    }

    private static void drawVerticalLine(PrintStream out) {
        setBlack(out);
        out.print(EMPTY);
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

    private static void setHighlight(PrintStream out) {
        out.print(SET_BG_COLOR_YELLOW);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setBackgroud(PrintStream out) {
        setBlack(out);
    }
}
