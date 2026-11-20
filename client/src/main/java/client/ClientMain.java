package client;

import chess.*;

import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        boolean loggedIn = false;
        Scanner scanner = new Scanner(System.in);

        while (true) {

            String line = scanner.nextLine().toLowerCase();

            switch (line) {
                case "help" -> help(loggedIn);
                case "quit" -> quit(loggedIn);
            }
        }
    }

    public static void help(boolean loggedIn){
        System.out.println("Available commands:");
        if(loggedIn){
            System.out.println("  help - Show available commands");
            System.out.println("  logout - Log out and return to the login menu");
            System.out.println("  create - Create a new game by providing a game name");
            System.out.println("  list - List games and their current players");
            System.out.println("  play - Join a game using its list number and choose white or black");
            System.out.println("  observe - Watch a game using its list number");
        } else {
            System.out.println("  help - Show available commands");
            System.out.println("  quit - Exit the program");
            System.out.println("  login - Log in with your username and password");
            System.out.println("  register - Create an account and log in");
        }
    }

    public static void quit(boolean loggedIn){
        if (loggedIn){
            logout();
        }
        System.exit(0);
    }

    public static void logout(){
        System.out.print("This has not been implemented, but logout was called");
    }
}
