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
                    promptManager.print(error.getErrorMessage(), true);
                    promptManager.printPrompt();
                },
                (game -> {
                    var chessGame = game.getGame().game();
                    clientContext.setGame(game.getGame());
                    promptManager.clearPrompt();
                    System.out.println();
                    BoardDisplay.show(chessGame, clientContext.getColor());
                    promptManager.printPrompt();
                }),
                (notification) -> {
                    promptManager.println(notification.getMessage(), true);
                    promptManager.printPrompt();
                }));
        clientContext.setPromptManager(promptManager);

        while (!promptManager.done()) {
            promptManager.printPrompt();
            promptManager.takeInput();
        }
    }
}
