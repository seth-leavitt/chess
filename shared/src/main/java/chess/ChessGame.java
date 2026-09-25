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
        this.board.resetBoard();
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

        // add castling moves
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            possible_moves.addAll(getCastlingMoves(startPosition));
        }

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

        // any old en_passant_flags need to be cleared
        clear_en_passant(current_turn);

        TeamColor opponent_color;
        if (current_turn == TeamColor.BLACK){
            opponent_color = TeamColor.WHITE;
        } else {
            opponent_color = TeamColor.BLACK;
        }

        ChessPiece moving_piece = this.board.getPiece(start_position);

        if (moving_piece == null){
            throw new InvalidMoveException("There is no piece here");
        }

        if (moving_piece.getTeamColor() != this.current_turn) {
            throw new InvalidMoveException("This is not the current player's piece");
        }

        Collection<ChessMove> valid_moves = validMoves(start_position);
        if (!valid_moves.contains(move)) {
            throw new InvalidMoveException("This is not a valid move for this piece");
        }

        // because the pawn moves are so different, we can just swap to that branch of logic so we don't have to it every time
        if (moving_piece.getPieceType() == ChessPiece.PieceType.PAWN){
            makePawnMove(move);
        } else if (moving_piece.getPieceType() == ChessPiece.PieceType.KING){
            makeKingMove(move);
        } else {
            this.board.addPiece(start_position, null);
            this.board.addPiece(end_position, moving_piece);

            // any piece that has moved can't castle
            moving_piece.setCastle_flag(false);

            // now we need to change the turn over
            setTeamTurn(opponent_color);
        }
    }

    private void makeKingMove(ChessMove move) {
        ChessPosition start_position = move.getStartPosition();
        ChessPosition end_position = move.getEndPosition();

        TeamColor opponent_color;
        if (current_turn == TeamColor.BLACK) {
            opponent_color = TeamColor.WHITE;
        } else {
            opponent_color = TeamColor.BLACK;
        }

        ChessPiece moving_piece = this.board.getPiece(start_position);

        int start_col = start_position.getColumn();
        int end_col = end_position.getColumn();
        int row = start_position.getRow();

        // Check if this is a castle
        if (Math.abs(end_col - start_col) == 2) {

            // Kingside castle
            if (end_col > start_col) {
                ChessPosition rook_start = new ChessPosition(row, 8);
                ChessPosition rook_end = new ChessPosition(row, end_col - 1);

                ChessPiece rook = this.board.getPiece(rook_start);

                // Move rook
                this.board.addPiece(rook_start, null);
                this.board.addPiece(rook_end, rook);

                rook.setCastle_flag(false);
            }

            // Queenside castle
            else {
                ChessPosition rook_start = new ChessPosition(row, 1);
                ChessPosition rook_end = new ChessPosition(row, end_col + 1);

                ChessPiece rook = this.board.getPiece(rook_start);

                // Move rook
                this.board.addPiece(rook_start, null);
                this.board.addPiece(rook_end, rook);

                rook.setCastle_flag(false);
            }
        }

        // Move king
        this.board.addPiece(start_position, null);
        this.board.addPiece(end_position, moving_piece);

        // King can never castle again after moving
        moving_piece.setCastle_flag(false);

        // Change turns
        setTeamTurn(opponent_color);
    }

    private Collection<ChessMove> getCastlingMoves(ChessPosition kingPosition) {
        Collection<ChessMove> castleMoves = new ArrayList<>();

        if (canCastle(kingPosition, true)) {
            castleMoves.add(
                    new ChessMove(
                            kingPosition,
                            new ChessPosition(
                                    kingPosition.getRow(),
                                    kingPosition.getColumn() + 2
                            ),
                            null
                    )
            );
        }

        if (canCastle(kingPosition, false)) {
            castleMoves.add(
                    new ChessMove(
                            kingPosition,
                            new ChessPosition(
                                    kingPosition.getRow(),
                                    kingPosition.getColumn() - 2
                            ),
                            null
                    )
            );
        }

        return castleMoves;
    }

    private boolean canCastle(ChessPosition kingPosition, boolean kingSide) {

        ChessPiece king = board.getPiece(kingPosition);

        // King must still have castling rights
        if (!king.isCastle_flag()) {
            return false;
        }

        if (isInCheck(king.getTeamColor())) {
            return false;
        }

        int row = kingPosition.getRow();
        int kingCol = kingPosition.getColumn();

        int rookCol;
        if (kingSide) {
            rookCol = 8;
        } else {
            rookCol = 1;
        }

        ChessPosition rookPosition = new ChessPosition(row, rookCol);
        ChessPiece rook = board.getPiece(rookPosition);

        // Make sure correct rook exists and hasn't moved
        if (rook == null
                || rook.getPieceType() != ChessPiece.PieceType.ROOK
                || rook.getTeamColor() != king.getTeamColor()
                || !rook.isCastle_flag()) {
            return false;
        }

        // Check all squares between king and rook
        int direction;
        if (kingSide) {
            direction = 1;
        } else {
            direction = -1;
        }

        for (int col = kingCol + direction;
             col != rookCol;
             col += direction) {

            if (board.getPiece(new ChessPosition(row, col)) != null) {
                return false;
            }
        }

        // King must safely cross the intermediate square
        ChessPosition middle =
                new ChessPosition(row, kingCol + direction);

        ChessMove middleMove =
                new ChessMove(kingPosition, middle, null);

        if (!testMove(middleMove)) {
            return false;
        }

        // King must not be in check when he gets there.
        ChessPosition destination =
                new ChessPosition(row, kingCol + 2 * direction);

        ChessMove castleMove =
                new ChessMove(kingPosition, destination, null);

        if (!testMove(castleMove)) {
            return false;
        }

        return true;
    }

    private void makePawnMove(ChessMove move){
        ChessPosition start_position = move.getStartPosition();
        ChessPosition end_position = move.getEndPosition();
        ChessPiece.PieceType promotion = move.getPromotionPiece();

        TeamColor opponent_color;
        if (current_turn == TeamColor.BLACK){
            opponent_color = TeamColor.WHITE;
        } else {
            opponent_color = TeamColor.BLACK;
        }

        ChessPiece moving_piece = this.board.getPiece(start_position);

        // if neither of these things are true, we're good to make the move
        // but first we can promote if we have to
        if (promotion != null){
            moving_piece = new ChessPiece(current_turn, promotion);
        }

        boolean enPassant = false;
        ChessPosition enPassantCapturePosition = null;

        if (moving_piece.getPieceType() == ChessPiece.PieceType.PAWN
                && start_position.getColumn() != end_position.getColumn()
                && this.board.getPiece(end_position) == null) {

            enPassant = true;

            enPassantCapturePosition =
                    new ChessPosition(start_position.getRow(), end_position.getColumn());
        }

        this.board.addPiece(start_position, null);
        this.board.addPiece(end_position, moving_piece);

        if (enPassant) {
            this.board.addPiece(enPassantCapturePosition, null);
        }

        // check to see if the move sets the en_passant flag
        if(moving_piece.getPieceType() == ChessPiece.PieceType.PAWN
                && (start_position.getRow() == 2 || start_position.getRow() == 7)
                && (end_position.getRow() == 4 || end_position.getRow() == 5)){
            moving_piece.setEn_passant_flag(true);
        }

        // now we need to change the turn over
        setTeamTurn(opponent_color);
    }

    private void clear_en_passant(TeamColor color){
        Collection<ChessPosition> pawn_positions = getRelevantPositions(color, ChessPiece.PieceType.PAWN);
        for(ChessPosition pawn_position : pawn_positions){
            ChessPiece pawn = this.board.getPiece(pawn_position);
            pawn.setEn_passant_flag(false);
        }
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

    private Collection<ChessPosition> getRelevantPositions(TeamColor teamColor, ChessPiece.PieceType type) {
        Collection<ChessPosition> relevantPositions = new ArrayList<>();

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {

                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                if (piece != null && piece.getPieceType() == type && piece.getTeamColor() == teamColor) {
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
        if (checkValidMovesAvailable(teamColor)) {
            return false;
        }
        return isInCheck(teamColor);
    }



    private boolean checkValidMovesAvailable(TeamColor teamColor) {
        Collection<ChessPosition> relevant_positions = getRelevantPositions(teamColor);

        Collection<ChessMove> possible_moves = new ArrayList<>();
        for (ChessPosition position : relevant_positions){
            possible_moves.addAll(validMoves(position));
            if (!possible_moves.isEmpty()){
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (checkValidMovesAvailable(teamColor)) {
            return false;
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
