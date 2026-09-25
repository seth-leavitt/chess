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

    private final ChessGame.TeamColor color;
    private final PieceType type;
    private boolean en_passant_flag = false;
    private boolean can_castle_flag = true;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return color == that.color && type == that.type;
    }

    @Override
    public String toString(){
        String value = "";

        switch(this.type){
            case KING -> value = "k";
            case QUEEN -> value = "q";
            case BISHOP -> value = "b";
            case KNIGHT -> value = "n";
            case ROOK -> value = "r";
            case PAWN -> value = "p";


        }
        if(this.color == ChessGame.TeamColor.WHITE){
            return value.toUpperCase();
        } else {
            return value.toLowerCase();
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type);
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
        return this.color;
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
        return switch (this.type) {
            case KING -> kingMoves(board, myPosition);
            case QUEEN -> queenMoves(board, myPosition);
            case ROOK -> rookMoves(board, myPosition);
            case BISHOP -> bishopMoves(board, myPosition);
            case KNIGHT -> knightMoves(board, myPosition);
            case PAWN -> pawnMoves(board, myPosition);
        };
    }

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition){
        int col_start = myPosition.getColumn();
        int row_start = myPosition.getRow();

        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int[][] directions = {{1,1}, {1,0}, {1,-1}, {0,1}, {0,-1}, {-1, 1}, {-1, 0}, {-1,-1}};
        for (int[] direction : directions) {
            ChessPosition newPosition = new ChessPosition(row_start + direction[0], col_start + direction[1]);
            if(isFree(board, newPosition)){
                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                possibleMoves.add(newMove);
            }else{
                if(canCapture(board, newPosition)){
                    ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                    possibleMoves.add(newMove);
                }
            }
        }
        return possibleMoves;
    }

    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        // just composite the two movesets.
        possibleMoves.addAll(rookMoves(board, myPosition));
        possibleMoves.addAll(bishopMoves(board, myPosition));

        return possibleMoves;
    }

    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition){
        int col_start = myPosition.getColumn();
        int row_start = myPosition.getRow();
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int[][] directions = {{1,0}, {-1,0}, {0,1}, {0,-1}};
        for (int[] direction : directions) {
            for(int x = 1; x <= 8 && x >= 1; x++){
                ChessPosition newPosition = new ChessPosition(row_start + x*direction[0], col_start + x*direction[1]);
                if(isFree(board, newPosition)){
                    ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                    possibleMoves.add(newMove);
                }else{
                    if(canCapture(board, newPosition)){
                        ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                        possibleMoves.add(newMove);
                    }
                    break;
                }
            }
        }
        return possibleMoves;
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition){
        int col_start = myPosition.getColumn();
        int row_start = myPosition.getRow();
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int[][] directions = {{1,1}, {-1,-1}, {-1,1}, {1,-1}};
        for (int[] direction : directions) {
            for(int x = 1; x <= 8 && x >= 1; x++){
                ChessPosition newPosition = new ChessPosition(row_start + x*direction[0], col_start + x*direction[1]);
                if(isFree(board, newPosition)){
                    ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                    possibleMoves.add(newMove);
                }else{
                    if(canCapture(board, newPosition)){
                        ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                        possibleMoves.add(newMove);
                    }
                    break;
                }
            }
        }
        return possibleMoves;
    }

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition){
        int col_start = myPosition.getColumn();
        int row_start = myPosition.getRow();
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int[][] directions = {{1,2}, {1,-2}, {-1, 2}, {-1, -2}, {2, 1}, {-2, 1}, {-2, -1}, {2, -1}};
        for (int[] direction : directions) {
            ChessPosition newPosition = new ChessPosition(row_start + direction[0], col_start + direction[1]);
            if(isFree(board, newPosition)){
                ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                possibleMoves.add(newMove);
            }else{
                if(canCapture(board, newPosition)){
                    ChessMove newMove = new ChessMove(myPosition, newPosition, null);
                    possibleMoves.add(newMove);
                }
            }
        }
        return possibleMoves;
    }

    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        int start_row = myPosition.getRow();
        int start_col = myPosition.getColumn();

        int direction = 1;
        if (this.color == ChessGame.TeamColor.BLACK){
            direction = -1;
        }

        // basic move
        ChessPosition basicNewPosition = new ChessPosition(start_row + direction, start_col);
        if (isFree(board, basicNewPosition)) {
            possibleMoves.addAll(addPawnMoves(myPosition, basicNewPosition));
        }

        // double move
        ChessPosition doubleNewPosition = new ChessPosition(start_row + 2*direction, start_col);
        if (isFree(board, basicNewPosition) && isFree(board, doubleNewPosition) && (start_row == 7 || start_row == 2)) {
            possibleMoves.addAll(addPawnMoves(myPosition, doubleNewPosition));
        }

        // capture left
        ChessPosition captureLeftPosition = new ChessPosition(start_row + direction, start_col-1);
        if(canCapture(board, captureLeftPosition)){
            possibleMoves.addAll(addPawnMoves(myPosition, captureLeftPosition));
        }

        //capture right
        ChessPosition captureRightPosition = new ChessPosition(start_row + direction, start_col+1);
        if(canCapture(board, captureRightPosition)){
            possibleMoves.addAll(addPawnMoves(myPosition, captureRightPosition));
        }

        // en passant balogne


        return possibleMoves;
    }

    private Collection<ChessMove> addPawnMoves(ChessPosition myPosition, ChessPosition newPosition){
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        int new_row = newPosition.getRow();

        if(new_row == 1 || new_row == 8){
            possibleMoves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
            possibleMoves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
            possibleMoves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
            possibleMoves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
        }else{
            possibleMoves.add(new ChessMove(myPosition, newPosition, null));
        }

        return possibleMoves;
    }

    private boolean isFree(ChessBoard board, ChessPosition newPosition){
        if (newPosition.getRow() >= 1 && newPosition.getRow() <= 8 && newPosition.getColumn() <= 8 && newPosition.getColumn() >= 1){
            ChessPiece currentInhabitant = board.getPiece(newPosition);
            return currentInhabitant == null;
        }
        return false;
    }

    private boolean canCapture(ChessBoard board, ChessPosition newPosition){
        if(newPosition.getRow() >= 1 && newPosition.getRow() <= 8 && newPosition.getColumn() <= 8 && newPosition.getColumn() >=1){
            ChessPiece currentInhabitant = board.getPiece(newPosition);
            if (currentInhabitant == null){
                return false;
            }
            return currentInhabitant.getTeamColor() != this.color;
        }
        return false;
    }

}