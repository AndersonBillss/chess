package handlers.websocket;

import chess.ChessGame;
import org.eclipse.jetty.websocket.api.Session;

public record SessionData(Session session, ChessGame.TeamColor color) {
}
