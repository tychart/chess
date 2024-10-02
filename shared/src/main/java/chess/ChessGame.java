package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor currTurn;
    private ChessBoard board;

    public ChessGame() {

        currTurn = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    public static TeamColor getOpposingTeamColor(TeamColor teamColor) {
        return teamColor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece currPiece = this.getBoard().getPiece(startPosition);

        if (currPiece == null) {
            return null;
        }

        Collection<ChessMove> validMoves = new HashSet<ChessMove>();
        Collection<ChessMove> allMoves = currPiece.pieceMoves(this.getBoard(), startPosition);


        for (ChessMove currMove : allMoves) {
            ChessBoard currPotentialBoard = new ChessBoard(this.getBoard());
            currPotentialBoard.unsafeMovePiece(currMove);

            // Make sure the current player's king does not become in check after that move is made
            if (!currPotentialBoard.king_in_check(currPiece.getTeamColor())) {
                validMoves.add(currMove);
            }
        }

        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (
                this.getBoard().getPiece(move.getStartPosition()) == null ||
                        this.getBoard().getPiece(move.getStartPosition()).getTeamColor() != this.getTeamTurn()
        ) {
            // Throw an exception if the piece doesn't exist or is not the player's piece
            throw new InvalidMoveException("Invalid move: " + move);
        }

        // Get the valid moves for the piece at the start position of the move
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());

        // Check if the move is in the set of valid moves
        if (validMoves != null && validMoves.contains(move)) {

            // Perform the move
            this.getBoard().unsafeMovePiece(move);
        } else {
            // Throw an exception if the move is invalid
            throw new InvalidMoveException("Invalid move: " + move);
        }
        // Switch turns after making a move
        setTeamTurn(getOpposingTeamColor(getTeamTurn()));
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return this.getBoard().king_in_check(teamColor);
    }

//    // Given a board, is the king in check?
//    private boolean willLeadToCheck(ChessBoard currBoard, TeamColor teamColor) {
//        Map<ChessPiece, ChessPosition> teamPieces = currBoard.getAllTeamPieces(teamColor);
//        HashSet<ChessMove> allMoves = new HashSet<ChessMove>();
//        HashSet<ChessPosition> allEndPositions = new HashSet<ChessPosition>();
//        ChessPosition kingPosition = currBoard.getKingPosition(teamColor);
//
//        for (Map.Entry<ChessPiece, ChessPosition> entry : teamPieces.entrySet()) {
//            ChessPiece piece = entry.getKey();
//            ChessPosition position = entry.getValue();
//            allMoves.addAll(piece.pieceMoves(currBoard, position));
//        }
//
//        for (ChessMove move : allMoves) {
//            allEndPositions.add(move.getEndPosition());
//        }
//
//        return allEndPositions.contains(kingPosition);
//    }


    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return tryEveryPosition(teamColor, true);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return tryEveryPosition(teamColor, false);
    }

    private boolean tryEveryPosition(TeamColor teamColor, boolean check_current) {
//        ChessGame.TeamColor opponentTeamColor = ChessGame.getOpposingTeamColor(teamColor);
        HashSet<ChessPosition> opponentPieces = getBoard().getAllTeamPieces(teamColor);
        HashSet<ChessMove> allMoves = new HashSet<>();


        for (ChessPosition position : opponentPieces) {
            allMoves.addAll(validMoves(position));
        }

        for (ChessMove currMove : allMoves) {
            ChessBoard currPotentialBoard = new ChessBoard(this.getBoard());
            currPotentialBoard.unsafeMovePiece(currMove);

            // Make sure the current player's king does not become in check after that move is made
            if (!currPotentialBoard.king_in_check(this.getTeamTurn())) {
                return false;
            }
        }

        // If checking for checkmate rather than stalemate, also check to see if in check
        // If checking for stalemate, then do not return true if there is a checkmate
        if (check_current) {
            return this.getBoard().king_in_check(this.getTeamTurn());
        } else if (this.getBoard().king_in_check(this.getTeamTurn())) {
            return false;
        }


        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
