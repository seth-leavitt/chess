package chess;
import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] board = new ChessPiece[9][9];

    public ChessBoard() {

    }

    public static void main(String[] args){
        ChessBoard chessBoard = new ChessBoard();
        chessBoard.resetBoard();
        System.out.println(chessBoard);
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        this.board[position.getRow()][position.getColumn()] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return this.board[position.getRow()][position.getColumn()];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = new ChessPiece[9][9];
        this.setupBoard();
    }

    private void setupBoard(){
        //set up colors
        ChessGame.TeamColor white = ChessGame.TeamColor.WHITE;
        ChessGame.TeamColor black = ChessGame.TeamColor.BLACK;

        // define piece types
        ChessPiece.PieceType pawn = ChessPiece.PieceType.PAWN;
        ChessPiece.PieceType rook = ChessPiece.PieceType.ROOK;
        ChessPiece.PieceType knight = ChessPiece.PieceType.KNIGHT;
        ChessPiece.PieceType bishop = ChessPiece.PieceType.BISHOP;
        ChessPiece.PieceType queen = ChessPiece.PieceType.QUEEN;
        ChessPiece.PieceType king = ChessPiece.PieceType.KING;

        // create copies of all of the chess pieces that will be used on the board

        // white
        ChessPiece white_pawn = new ChessPiece(white, pawn);
        ChessPiece white_rook = new ChessPiece(white, rook);
        ChessPiece white_knight = new ChessPiece(white, knight);
        ChessPiece white_bishop = new ChessPiece(white, bishop);
        ChessPiece white_queen = new ChessPiece(white, queen);
        ChessPiece white_king = new ChessPiece(white, king);

        //black
        ChessPiece black_pawn = new ChessPiece(black, pawn);
        ChessPiece black_rook = new ChessPiece(black, rook);
        ChessPiece black_knight = new ChessPiece(black, knight);
        ChessPiece black_bishop = new ChessPiece(black, bishop);
        ChessPiece black_queen = new ChessPiece(black, queen);
        ChessPiece black_king = new ChessPiece(black, king);

        // now we can place white
        this.addPiece(new ChessPosition(1,1), white_rook);
        this.addPiece(new ChessPosition(1,2), white_knight);
        this.addPiece(new ChessPosition(1,3), white_bishop);
        this.addPiece(new ChessPosition(1,4), white_queen);
        this.addPiece(new ChessPosition(1,5), white_king);
        this.addPiece(new ChessPosition(1, 6), white_bishop);
        this.addPiece(new ChessPosition(1,7), white_knight);
        this.addPiece(new ChessPosition(1, 8), white_rook);
        // white pawns
        for(int i = 1; i < 9; i++){
            this.addPiece(new ChessPosition(2,i), white_pawn);
        }

        // now we can place black
        this.addPiece(new ChessPosition(8,1), black_rook);
        this.addPiece(new ChessPosition(8,2), black_knight);
        this.addPiece(new ChessPosition(8,3), black_bishop);
        this.addPiece(new ChessPosition(8,4), black_queen);
        this.addPiece(new ChessPosition(8,5), black_king);
        this.addPiece(new ChessPosition(8, 6), black_bishop);
        this.addPiece(new ChessPosition(8,7), black_knight);
        this.addPiece(new ChessPosition(8, 8), black_rook);
        // black pawns
        for(int i = 1; i < 9; i++){
            this.addPiece(new ChessPosition(7,i), black_pawn);
        }

    }

    @Override
    public String toString(){
        String boardString = Arrays.deepToString(this.board);
        return "chess.ChessBoard@"+boardString;
    }

    @Override
    public boolean equals(Object o){
        if(o==this){return true;}
        if(o == null || o.getClass() != this.getClass()){return false;}
        ChessBoard other = (ChessBoard) o;
        return Arrays.deepEquals(this.board, other.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}
