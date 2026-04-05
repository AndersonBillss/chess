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

        ServerFacade serverFacade = new ServerFacade("http://localhost:8080");
        WebSocketFacade socketFacade = new WebSocketFacade("http://localhost:8080",
                (error) -> {
                    System.err.println(error.getErrorMessage());
                },
                (game -> {
                    System.out.println("LOAD GAME");
                }),
                (notification) -> {
                    System.out.println(notification.getMessage());
                });

        UI currUI = new PreloginUI(new ClientContext(serverFacade, socketFacade));

        while (currUI != null) {
            currUI = promptManager.takeInput(currUI);
        }
    }
}
