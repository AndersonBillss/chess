package client;

import chess.ChessGame;
import model.GameData;
import server.ServerFacade;
import server.WebSocketFacade;

public class ClientContext {
    private ServerFacade serverFacade;
    private WebSocketFacade webSocketFacade;
    private GameData game;
    private ChessGame.TeamColor color;
    private PromptManager promptManager;

    public void setServerFacade(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public void setWebSocketFacade(WebSocketFacade webSocketFacade) {
        this.webSocketFacade = webSocketFacade;
    }

    public ServerFacade getServerFacade() {
        return serverFacade;
    }

    public WebSocketFacade getWebSocketFacade() {
        return webSocketFacade;
    }

    public ChessGame.TeamColor getColor() {
        return color;
    }

    public void setColor(ChessGame.TeamColor color) {
        this.color = color;
    }

    public PromptManager getPromptManager() {
        return promptManager;
    }

    public void setPromptManager(PromptManager promptManager) {
        this.promptManager = promptManager;
    }

    public GameData getGame() {
        return game;
    }

    public void setGame(GameData game) {
        this.game = game;
    }
}
