package client.responses;

public class CreateGameResponse {
    final int gameID;

    public CreateGameResponse(int gameID) {

        this.gameID = gameID;
    }

    public int getGameID() {

        return gameID;
    }
}
