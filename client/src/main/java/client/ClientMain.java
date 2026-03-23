package client;

import chess.*;
import client.ui.*;
import server.ServerFacade;

public class ClientMain {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        PromptManager promptManager = new PromptManager();
        ServerFacade serverFacade = new ServerFacade("http://localhost:8080");

        UI currUI = new PreloginUI(serverFacade);

        while (currUI != null) {
            currUI = promptManager.takeInput(currUI);
        }
    }
}
