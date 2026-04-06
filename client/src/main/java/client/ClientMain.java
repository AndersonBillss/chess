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


        ClientContext clientContext = new ClientContext();
        PromptManager promptManager = new PromptManager(new PreloginUI(clientContext));
        clientContext.setServerFacade(new ServerFacade("http://localhost:8080"));
        clientContext.setWebSocketFacade(new WebSocketFacade("http://localhost:8080",
                (error) -> {
                    System.err.println(error.getErrorMessage());
                    promptManager.printPrompt();
                },
                (game -> {
                    var board = game.getGame().game().getBoard();
                    clientContext.setBoard(board);
                    BoardDisplay.show(board, clientContext.getColor());
                    promptManager.printPrompt();
                }),
                (notification) -> {
                    System.out.println(notification.getMessage());
                    promptManager.printPrompt();
                }));
        clientContext.setPromptManager(promptManager);

        promptManager.printPrompt();
        while (!promptManager.done()) {
            promptManager.takeInput();
        }
    }
}
