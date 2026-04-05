package client;

import chess.*;
import client.ui.*;
import exception.ResponseException;
import server.ServerFacade;
import server.WebSocketFacade;

public class ClientMain {
    public static void main(String[] args) throws ResponseException {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        PromptManager promptManager = new PromptManager();

        ClientContext clientContext;
        clientContext = new ClientContext();
        clientContext.setServerFacade(new ServerFacade("http://localhost:8080"));
        clientContext.setWebSocketFacade(new WebSocketFacade("http://localhost:8080",
                (error) -> {
                    System.err.println(error.getErrorMessage());
                },
                (game -> {
                    var board = game.getGame().getBoard();
                    clientContext.setBoard(board);
                    System.out.println();
                    BoardDisplay.show(board, clientContext.getColor());
                }),
                (notification) -> {
                    System.out.println(notification.getMessage());
                }));
        UI currUI = new PreloginUI(clientContext);

        while (currUI != null) {
            currUI = promptManager.takeInput(currUI);
        }
    }
}
