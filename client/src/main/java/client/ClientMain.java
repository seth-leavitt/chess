package client;

import chess.*;
import model.GameSummary;
import ui.ChessBoardRenderer;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ClientMain {
    private static final ServerFacade FACADE = new ServerFacade(8080);
    private static final Scanner SCANNER = new Scanner(System.in);
    private static String authToken;
    private static List<GameSummary> games = List.of();

    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        help(false);

        while (SCANNER.hasNextLine()) {

            String line = SCANNER.nextLine().trim().toLowerCase();
            boolean loggedIn = authToken != null;

            try {
                executeCommand(line, loggedIn);
            } catch (IOException ex){
                System.out.println("Could not complete " + line + ". Check your information and make sure the server is running.");
            } catch (IllegalArgumentException ex){
                System.out.println(ex.getMessage());
            } catch (NoSuchElementException ex){
                break;
            }
        }
    }

    private static void executeCommand(String line, boolean loggedIn) throws IOException {
        if(loggedIn && (line.equals("login") || line.equals("register"))){
            System.out.println("Please log out first.");
            return;
        }
        if(!loggedIn && List.of("logout", "create", "list", "play", "observe").contains(line)){
            System.out.println("Please log in first.");
            return;
        }
        switch (line){
            case "help" -> help(loggedIn);
            case "quit" -> quit(loggedIn);
            case "login" -> login();
            case "register" -> register();
            case "logout" -> logout();
            case "create" -> createGame();
            case "list" -> listGames();
            case "play" -> playGame();
            case "observe" -> observeGame();
            default -> System.out.println("Unknown command. Type help to see available commands.");
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
            System.out.println("  quit - Log out and exit the program");
        } else {
            System.out.println("  help - Show available commands");
            System.out.println("  quit - Exit the program");
            System.out.println("  login - Log in with your username and password");
            System.out.println("  register - Create an account and log in");
        }
    }

    public static void quit(boolean loggedIn) throws IOException {
        if (loggedIn){
            logout();
        }
        System.exit(0);
    }

    public static void logout() throws IOException {
        FACADE.logout(authToken);
        authToken = null;
        games = List.of();
        System.out.println("You are logged out.");
        help(false);
    }

    public static void login() throws IOException {
        String username = prompt("Username:");
        String password = prompt("Password:");
        var response = FACADE.login(username, password);
        authToken = response.getAuthToken();
        System.out.println("Welcome, " + response.getUsername() + "!");
        help(true);
    }

    public static void register() throws IOException {
        String username = prompt("Username:");
        String password = prompt("Password:");
        String email = prompt("Password:");
        var response = FACADE.register(username, password, email);
        authToken = response.getAuthToken();
        System.out.println("Welcome, " + response.getUsername() + "!");
        help(true);
    }

    public static void createGame() throws IOException {
        String gameName = prompt("Game name:");
        FACADE.createGame(authToken, gameName);
        System.out.println("Created " + gameName + ". Use list to find it and play to join.");
    }

    public static void listGames() throws IOException {
        var response = FACADE.listGames(authToken);
        games = response.getGames();
        if(games.isEmpty()){
            System.out.println("There are no games yet. Use create to make one.");
        }
        for(int i = 0; i < games.size(); i++){
            var game = games.get(i);
            String white = game.getWhiteUsername() == null ? "Open" : game.getWhiteUsername();
            String black = game.getBlackUsername() == null ? "Open" : game.getBlackUsername();
            System.out.println((i + 1) + ". " + game.getGameName() + " - White: " + white + ", Black: " + black);
        }
    }

    public static void playGame() throws IOException {
        var game = selectGame();
        if(game == null){
            return;
        }
        String color = prompt("Color (white or black):");
        ChessGame.TeamColor playerColor;
        if(color.equalsIgnoreCase("white")){
            playerColor = ChessGame.TeamColor.WHITE;
        } else if(color.equalsIgnoreCase("black")){
            playerColor = ChessGame.TeamColor.BLACK;
        } else {
            throw new IllegalArgumentException("Please choose white or black.");
        }
        FACADE.joinGame(authToken, playerColor, game.getGameID());
        System.out.println("Joined " + game.getGameName() + ".");
        drawBoard(playerColor);
    }

    public static void observeGame(){
        var game = selectGame();
        if(game == null){
            return;
        }
        System.out.println("Observing " + game.getGameName() + ".");
        drawBoard(ChessGame.TeamColor.WHITE);
    }

    private static GameSummary selectGame(){
        if(games.isEmpty()){
            System.out.println("Use list to see the available games first.");
            return null;
        }
        int number;
        try {
            number = Integer.parseInt(prompt("Game number from list:"));
        } catch (NumberFormatException ex){
            throw new IllegalArgumentException("Please enter a whole number from the game list.");
        }
        if(number < 1 || number > games.size()){
            throw new IllegalArgumentException("Please choose a number from the game list.");
        }
        return games.get(number - 1);
    }

    private static void drawBoard(ChessGame.TeamColor perspective){
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        ChessBoardRenderer.drawChessBoard(board, perspective);
        System.out.print("\u001b[0m");
    }

    private static String prompt(String message){
        System.out.println(message);
        String value = SCANNER.nextLine().trim();
        if(value.isEmpty()){
            throw new IllegalArgumentException("Please enter a value.");
        }
        return value;
    }
}
