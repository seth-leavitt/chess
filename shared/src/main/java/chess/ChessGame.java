package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    ChessBoard board = new ChessBoard();
    TeamColor current_turn = TeamColor.WHITE;

    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.current_turn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.current_turn = team;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && current_turn == chessGame.current_turn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, current_turn);
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = this.board.getPiece(startPosition);
//        System.out.println("Checking: " + piece);
        if (piece == null) {
            return null;
        }

        Collection<ChessMove> possible_moves = piece.pieceMoves(this.board, startPosition);
        possible_moves.removeIf(move -> !testMove(move));
        return possible_moves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start_position = move.getStartPosition();
        ChessPosition end_position = move.getEndPosition();

        TeamColor opponent_color = null;
        if (current_turn == TeamColor.BLACK){
            opponent_color = TeamColor.WHITE;
        } else {
            opponent_color = TeamColor.BLACK;
        }

        ChessPiece moving_piece = this.board.getPiece(start_position);

        if (moving_piece == null){
            return;
        }

        if (moving_piece.getTeamColor() != this.current_turn) {
            throw new InvalidMoveException("This is not the current player's piece");
        }

        Collection<ChessMove> valid_moves = validMoves(start_position);
        if (!valid_moves.contains(move)) {
            throw new InvalidMoveException("This is not a valid move for this piece");
        }

        // if neither of these things are true, we're good to make the move
        this.board.addPiece(start_position, null);
        this.board.addPiece(end_position, moving_piece);

        // now we need to change the turn over
        setTeamTurn(opponent_color);
    }

    private boolean testMove(ChessMove move) {
        ChessPosition start_position = move.getStartPosition();
        ChessPosition end_position = move.getEndPosition();

        ChessPiece moving_piece = this.board.getPiece(start_position);
        ChessPiece original_piece = this.board.getPiece(end_position);
        TeamColor moving_team = moving_piece.getTeamColor();


        // first we can just do the move
        this.board.addPiece(start_position, null);
        this.board.addPiece(end_position, moving_piece);

        // now we check if we are in check
        boolean checked = isInCheck(moving_team);

        // now we undo the move
        this.board.addPiece(start_position, moving_piece);
        this.board.addPiece(end_position, original_piece);

        // report the results
        return !checked;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        TeamColor opposing_color;

        // first we need to find the king
       ChessPosition king_position = findKing(teamColor);

        if (teamColor == TeamColor.BLACK) {
            opposing_color = TeamColor.WHITE;
        } else {
            opposing_color = TeamColor.BLACK;
        }
        // now we can reset positions to check to look for all the black pieces
        Collection<ChessPosition> positions_to_check = getRelevantPositions(opposing_color);
        Collection<ChessMove> possible_opponent_moves = new ArrayList<>();
        for (ChessPosition position : positions_to_check) {
            ChessPiece opponent_piece = this.board.getPiece(position);
            possible_opponent_moves.addAll(opponent_piece.pieceMoves(this.board, position));
        }
        Collection<ChessPosition> possible_opponent_positions = new ArrayList<>();
        for (ChessMove move : possible_opponent_moves) {
            possible_opponent_positions.add(move.getEndPosition());
        }
        for (ChessPosition position : possible_opponent_positions) {
//            System.out.println("Piece that could be attacked: " + this.board.getPiece(position));
            if (position.equals(king_position)){
                return true;
            }
        }
        return false;
    }

    private ChessPosition findKing(TeamColor teamColor){
        Collection<ChessPosition> positions_to_check = getRelevantPositions(teamColor);
        ChessPosition king_position = null;
        for (ChessPosition position : positions_to_check){
            if(this.board.getPiece(position).getPieceType() == ChessPiece.PieceType.KING){
                king_position = position;
            }
        }
        return king_position;
    }

    private Collection<ChessPosition> getRelevantPositions(TeamColor teamColor) {
        Collection<ChessPosition> relevantPositions = new ArrayList<>();

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {

                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                if (piece != null && piece.getTeamColor() == teamColor) {
                    relevantPositions.add(position);
                }
            }
        }

        return relevantPositions;
    }
    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        ChessPosition king_position = findKing(teamColor);
        Collection<ChessMove> potential_moves = validMoves(king_position);

        return potential_moves.isEmpty() && isInCheck(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        Collection<ChessPosition> relevant_positions = getRelevantPositions(teamColor);

        Collection<ChessMove> possible_moves = new ArrayList<>();
        for (ChessPosition position : relevant_positions){
            possible_moves.addAll(validMoves(position));
            if (!possible_moves.isEmpty()){
                return false;
            }
        }
        return !isInCheck(teamColor);
    }

    /**
     * Sets this game's chessboard to a given board
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
