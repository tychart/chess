import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


import ui.TestUIPrint;

public class UITest {
    static ChessGame chessGame;

    @BeforeAll
    public static void init() {
        chessGame = new ChessGame();

    }

    @Test
    public void testPrint() throws InvalidMoveException {
        chessGame.makeMove(new ChessMove(
                new ChessPosition(2, 1),
                new ChessPosition(4, 1),
                null
        ));
        chessGame.makeMove(new ChessMove(
                new ChessPosition(7, 3),
                new ChessPosition(6, 3),
                null
        ));
        TestUIPrint printBoard = new TestUIPrint(chessGame);
        assertTrue(true);
    }
}

