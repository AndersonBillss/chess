package client;

import chess.ChessBoard;
import chess.ChessGame;
import server.ServerFacade;
import server.WebSocketFacade;

public class ClientContext {
    private ServerFacade serverFacade;
    private WebSocketFacade webSocketFacade;
    private ChessBoard board;
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

    public ChessBoard getBoard() {
        return board;
    }

    public void setBoard(ChessBoard board) {
        this.board = board;
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
}
