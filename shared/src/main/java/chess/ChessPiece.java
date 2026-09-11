package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private PieceType type;
    private ChessGame.TeamColor pieceColor;
    private boolean unmoved;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.type = type;
        this.pieceColor = pieceColor;
        this.unmoved = true;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return type == that.type && pieceColor == that.pieceColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, pieceColor);
    }

    @Override
    public String toString(){
        switch (this.type){
            case KING -> {return "K";}
            case QUEEN -> {return "Q";}
            case ROOK -> {return "R";}
            case KNIGHT -> {return "K";}
            case BISHOP -> {return "B";}
            case PAWN -> {return "P";}
        }
        return "#";
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        switch (this.type){
            case KING -> {return kingMoves(board, myPosition);}
            case QUEEN -> {return queenMoves(board, myPosition);}
            case ROOK -> {return rookMoves(board, myPosition);}
            case KNIGHT -> {return knightMoves(board, myPosition);}
            case BISHOP -> {return bishopMoves(board, myPosition);}
            case PAWN -> {return pawnMoves(board, myPosition);}
        }
        return null;
    }

    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        int pawnDirection;
        int pawnPromotionRow;

        if (this.getTeamColor() == ChessGame.TeamColor.WHITE){
            pawnDirection = 1;
            pawnPromotionRow = 8;
        } else {
            pawnDirection = -1;
            pawnPromotionRow = 1;
        }

        Collection<ChessMove> possible_moves = new ArrayList<>();

        // one move forward
        ChessPosition new_position = new ChessPosition(row + pawnDirection, col);
        if (board.getPiece(new_position) == null){
            ChessMove new_move = new ChessMove(myPosition, new_position, null);
            if (row + pawnDirection == pawnPromotionRow){
                possible_moves.addAll(pawnPromotionMoves(myPosition, new_position));
            } else {
                possible_moves.add(new_move);
            }
        }

        // double move from starting spot
        if ((myPosition.getRow() == 2 && this.getTeamColor() == ChessGame.TeamColor.WHITE) || (myPosition.getRow() == 7 && this.getTeamColor() == ChessGame.TeamColor.BLACK)){
            ChessPosition new_position_2 = new ChessPosition(row + (2*pawnDirection), col);
            if (board.getPiece(new_position_2) == null && (board.getPiece(new_position) == null)) {
                ChessMove new_move_2 = new ChessMove(myPosition, new_position_2, null);
                possible_moves.add(new_move_2);
            }
        }

        // captures
        if (col - 1 >= 1){ // pawn is legally allowed to move left
            ChessPosition capture_position_left = new ChessPosition(row + pawnDirection, col - 1);
            if (opposing_team(board, capture_position_left) && (row + pawnDirection != pawnPromotionRow)){
                // capture to the left but don't promote
                ChessMove capture_left = new ChessMove(myPosition, capture_position_left, null);
                possible_moves.add(capture_left);
            }
            else if (opposing_team(board, capture_position_left) && (row + pawnDirection == pawnPromotionRow)){
                possible_moves.addAll(pawnPromotionMoves(myPosition, capture_position_left));
            }
        }
        if (col + 1 <= 8){ // pawn is legally allowed to move right
            ChessPosition capture_position_right = new ChessPosition(row + pawnDirection, col + 1);
            if (opposing_team(board, capture_position_right) && (row + pawnDirection != pawnPromotionRow)){
                // capture to the right but don't promote
                ChessMove capture_right = new ChessMove(myPosition, capture_position_right, null);
                possible_moves.add(capture_right);
            }
            else if (opposing_team(board, capture_position_right) && (row + pawnDirection == pawnPromotionRow)){
                // capture to the left and add all the promotion options
                possible_moves.addAll(pawnPromotionMoves(myPosition, capture_position_right));
            }
        }

        // promotion

        return possible_moves;
    }

    private Collection<ChessMove> pawnPromotionMoves(ChessPosition myPosition, ChessPosition new_position){
        Collection<ChessMove> possible_moves = new ArrayList<>();

        // rook
        ChessMove capture_left_rook = new ChessMove(myPosition, new_position, PieceType.ROOK);
        possible_moves.add(capture_left_rook);

        // knight
        ChessMove capture_left_knight = new ChessMove(myPosition, new_position, PieceType.KNIGHT);
        possible_moves.add(capture_left_knight);

        // bishop
        ChessMove capture_left_bishop = new ChessMove(myPosition, new_position, PieceType.BISHOP);
        possible_moves.add(capture_left_bishop);

        // queen
        ChessMove capture_left_queen = new ChessMove(myPosition, new_position, PieceType.QUEEN);
        possible_moves.add(capture_left_queen);

        return possible_moves;
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        Collection<ChessMove> possible_moves = new ArrayList<>();

        //moving up-left
        for(int i = 1; (row + i <= 8 && col - i >= 1); i++){
            ChessPosition new_position = new ChessPosition(row + i, col - i);
            if(board.getPiece(new_position) == null) {
                // now that we now that this move is possible, we can make a move out of it
                ChessMove new_move = new ChessMove(myPosition, new_position, null);
                possible_moves.add(new_move);
            } else {
                // we need to check to see if we can capture, but we can't go further up
                if(opposing_team(board, new_position)){
                    // we can capture!
                    ChessMove new_move = new ChessMove(myPosition, new_position, null);
                    possible_moves.add(new_move);
                }
                break;
            }
        }
        // try moving up-right
        for(int i = 1; (row + i <= 8 && col + i <= 8); i++){
            ChessPosition new_position = new ChessPosition(row + i, col + i);
            if(board.getPiece(new_position) == null) {
                // now that we now that this move is possible, we can make a move out of it
                ChessMove new_move = new ChessMove(myPosition, new_position, null);
                possible_moves.add(new_move);
            } else {
                // we need to check to see if we can capture, but we can't go further up
                if(opposing_team(board, new_position)){
                    // we can capture!
                    ChessMove new_move = new ChessMove(myPosition, new_position, null);
                    possible_moves.add(new_move);
                }
                break;
            }
        }
        // try moving down-left
        for(int i = 1; (row - i >= 1 && col - i >= 1); i++){
            ChessPosition new_position = new ChessPosition(row - i, col - i);
            if(board.getPiece(new_position) == null) {
                // now that we now that this move is possible, we can make a move out of it
                ChessMove new_move = new ChessMove(myPosition, new_position, null);
                possible_moves.add(new_move);
            } else {
                // we need to check to see if we can capture, but we can't go further up
                if(opposing_team(board, new_position)){
                    // we can capture!
                    ChessMove new_move = new ChessMove(myPosition, new_position, null);
                    possible_moves.add(new_move);
                }
                break;
            }
        }
        // try moving down-right
        for(int i = 1; (row - i >= 1 && col + i <= 8); i++){
            ChessPosition new_position = new ChessPosition(row - i, col + i);
            if(board.getPiece(new_position) == null) {
                // now that we now that this move is possible, we can make a move out of it
                ChessMove new_move = new ChessMove(myPosition, new_position, null);
                possible_moves.add(new_move);
            } else {
                // we need to check to see if we can capture, but we can't go further up
                if(opposing_team(board, new_position)){
                    // we can capture!
                    ChessMove new_move = new ChessMove(myPosition, new_position, null);
                    possible_moves.add(new_move);
                }
                break;
            }
        }
        // we've now collected all the bishop moves
        return possible_moves;
    }

    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        Collection<ChessMove> possible_moves = new ArrayList<>();

        // try moving up
        for(int i = row + 1; i <= 8; i++){
            ChessPosition new_position = new ChessPosition(i, col);
            if(board.getPiece(new_position) == null) {
                // now that we now that this move is possible, we can make a move out of it
                ChessMove new_move = new ChessMove(myPosition, new_position, null);
                possible_moves.add(new_move);
            } else {
                // we need to check to see if we can capture, but we can't go further up
                if(opposing_team(board, new_position)){
                    // we can capture!
                    ChessMove new_move = new ChessMove(myPosition, new_position, null);
                    possible_moves.add(new_move);
                }
                break;
            }
        }
        // try moving down
        for(int i = row - 1; i >= 1; i--){
            ChessPosition new_position = new ChessPosition(i, col);
            if(board.getPiece(new_position) == null) {
                // now that we now that this move is possible, we can make a move out of it
                ChessMove new_move = new ChessMove(myPosition, new_position, null);
                possible_moves.add(new_move);
            } else {
                // we need to check to see if we can capture, but we can't go further up
                if(opposing_team(board, new_position)){
                    // we can capture!
                    ChessMove new_move = new ChessMove(myPosition, new_position, null);
                    possible_moves.add(new_move);
                }
                break;
            }
        }
        // try moving left
        for(int i = col - 1; i >= 1; i--){
            ChessPosition new_position = new ChessPosition(row, i);
            if(board.getPiece(new_position) == null) {
                // now that we now that this move is possible, we can make a move out of it
                ChessMove new_move = new ChessMove(myPosition, new_position, null);
                possible_moves.add(new_move);
            } else {
                // we need to check to see if we can capture, but we can't go further up
                if(opposing_team(board, new_position)){
                    // we can capture!
                    ChessMove new_move = new ChessMove(myPosition, new_position, null);
                    possible_moves.add(new_move);
                }
                break;
            }
        }
        // try moving right
        for(int i = col + 1; i <= 8; i++){
            ChessPosition new_position = new ChessPosition(row, i);
            if(board.getPiece(new_position) == null) {
                // now that we now that this move is possible, we can make a move out of it
                ChessMove new_move = new ChessMove(myPosition, new_position, null);
                possible_moves.add(new_move);
            } else {
                // we need to check to see if we can capture, but we can't go further up
                if(opposing_team(board, new_position)){
                    // we can capture!
                    ChessMove new_move = new ChessMove(myPosition, new_position, null);
                    possible_moves.add(new_move);
                }
                break;
            }
        }

        // we've now collected all the rook moves
        return possible_moves;
    }

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        Collection<ChessMove> possible_moves = new ArrayList<>();

        // up/down
        int magnitude = 2;
        for (int i = -1; i <= 1; i++){
            //left/right
            for (int j = -1; j <= 1; j++){
                if (i != 0 && j != 0){
                    ChessPosition newPosition = new ChessPosition(row+i*magnitude, col + j);
                    if((row+i*magnitude >= 1 && row+i*magnitude <= 8) && (col+j >= 1 && col+j <= 8)) {
                        if (opposing_team(board, newPosition) || free_space(board, newPosition)) {
                            ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                            possible_moves.add(newMove);
                        }
                    }
                }
            }
        }
        // left/right
        for (int i = -1; i <= 1; i++){
            // up/down
            for (int j = -1; j <= 1; j++){
                if (i != 0 && j != 0){
                    ChessPosition newPosition = new ChessPosition(row + j, col + i*magnitude);
                    if((row+j >= 1 && row+j <= 8) && (col+i*magnitude >= 1 && col+i*magnitude <= 8)) {
                        if (opposing_team(board, newPosition) || free_space(board, newPosition)) {
                            ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                            possible_moves.add(newMove);
                        }
                    }
                }
            }
        }

        return possible_moves;
    }

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        Collection<ChessMove> possible_moves = new ArrayList<>();

        for (int i = row - 1; i <= row + 1; i++){ // searching the whole range of possibilities
            for (int j = col - 1; j <= col + 1; j++){
                if ((i <= 8 && i >= 1) && (j <= 8 && j >= 1)){ // if it's a real board spot...
                    ChessPosition new_position = new ChessPosition(i, j);
                    if(board.getPiece(new_position) == null || opposing_team(board, new_position)){
                        ChessMove new_move = new ChessMove(myPosition, new_position, null);
                        possible_moves.add(new_move);
                    }
                }
            }
        }
        return possible_moves;
    }

    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possible_moves = new ArrayList<>();

        // the queen is just the combo of the rook and the bishop
        possible_moves.addAll(bishopMoves(board, myPosition));
        possible_moves.addAll(rookMoves(board, myPosition));

        return possible_moves;
    }

    private boolean opposing_team(ChessBoard board, ChessPosition new_position){
        if (board.getPiece(new_position) == null){
            return false;
        }
        return board.getPiece(new_position).getTeamColor() != this.getTeamColor();
    }

    private boolean free_space(ChessBoard board, ChessPosition new_position){
        return board.getPiece(new_position) == null;
    }
}
