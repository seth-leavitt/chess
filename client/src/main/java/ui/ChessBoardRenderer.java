package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

public class ChessBoardRenderer {

    ChessBoardRenderer(ChessBoard board){
    }

    public static void main(String[] args) {
        ChessBoard newBoard = new ChessBoard();
        newBoard.resetBoard();
        drawChessBoard(newBoard);
    }

    public static void drawChessBoard(ChessBoard board){
        drawChessBoard(board, ChessGame.TeamColor.WHITE);
    }

    public static void drawChessBoard(ChessBoard board, ChessGame.TeamColor perspective){
        for(int y = 0; y <= 9; y++){
            for(int x = 0; x <= 9; x++){ // we want to add a border of grayness
                int row = 9 - y;
                int column = x;
                if (perspective == ChessGame.TeamColor.BLACK){
                    row = y;
                    column = 9 - x;
                }
                if (y == 0 || y == 9){
                    String spaceString = "\u001b[39;49;1m   ";
                    if (x >= 1 && x <= 8){
                        spaceString = "\u001b[39;49;1m " + (char) ('a' + column - 1) + " "; // I initially had no idea I could do this.
                        // AI suggested it as a better way to iterate through them and so I used it.
                    }
                    System.out.print(spaceString);
                } else if (x == 0 || x ==9){
                    String spaceString = "\u001b[39;49;1m " + row + " ";
                    System.out.print(spaceString);
                } else { // now we know that we're dealing the board itself
                    ChessPosition position = new ChessPosition(row, column);
                    ChessPiece piece = board.getPiece(position);
                    String spaceString = "";
                    if ((column % 2 == 0) ^ !(row % 2 == 0)) { // this should not be the case on the leftmost 1, 1 space
                        spaceString += "\u001b[107;1m"; //101 is light red
                    } else {
                        spaceString += "\u001b[40;1m"; //104 is light blue
                    }
                    if (piece != null) {
                        spaceString += parseChessPiece(piece);
                    } else {
                        spaceString += "   ";
                    }
                    System.out.print(spaceString);
                }
            }
            System.out.println();
        }
    }

    static String parseChessPiece(ChessPiece piece){
        String returnedString = "";
        ChessGame.TeamColor pieceColor = piece.getTeamColor();
        if (pieceColor == ChessGame.TeamColor.WHITE){
            returnedString += "\u001b[36;1m";
        } else {
            returnedString += "\u001b[31;1m";
        }

        ChessPiece.PieceType pieceType = piece.getPieceType();
        switch (pieceType){
            case KING -> returnedString += " K ";
            case QUEEN -> returnedString += " Q ";
            case BISHOP -> returnedString += " B ";
            case ROOK -> returnedString += " R ";
            case PAWN -> returnedString += " P ";
            case KNIGHT -> returnedString += " N ";
            case null, default -> returnedString += " # "; // this is for error fidning
        }

        return returnedString;
    }
}
