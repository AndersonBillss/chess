package handlers.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SessionManager {
    public final Map<Integer, ArrayList<SessionData>> gameSessions;

    public SessionManager() {
        this.gameSessions = new HashMap<>();
    }

    public void addSession(int gameId, Session session, ChessGame.TeamColor color) {
        var sessions = gameSessions.get(gameId);
        if (sessions == null) {
            sessions = new ArrayList<>();
            gameSessions.put(gameId, sessions);
        }
        sessions.add(new SessionData(session, color));
    }

    public SessionData findSession(int gameId, Session session) {
        var sessions = gameSessions.get(gameId);
        if (sessions == null) {
            return null;
        }
        for (var s : sessions) {
            if (Objects.equals(s.session(), session)) {
                return s;
            }
        }
        return null;
    }

    public void removeSession(int gameId, Session session) {
        var sessions = gameSessions.get(gameId);
        if (sessions == null) {
            return;
        }
        Integer sessionIndex = null;
        for (int i = 0; i < sessions.size(); i++) {
            if (Objects.equals(sessions.get(i).session(), session)) {
                sessionIndex = i;
                break;
            }
        }
        if (sessionIndex == null) {
            return;
        }
        sessions.remove(sessionIndex.intValue());
    }

    public void broadcast(int gameId, Session excludedSession, String message) throws IOException {
        var sessions = gameSessions.get(gameId);
        if (sessions == null) {
            return;
        }
        for (var session : sessions) {
            if (!Objects.equals(session.session(), excludedSession)) {
                String response = new Gson().toJson(new NotificationMessage(message));
                session.session().getRemote().sendString(response);
            }
        }
    }

    public void broadcast(GameData game) throws IOException {
        broadcast(game, null);
    }

    public void broadcast(GameData game, Session excludedSession) throws IOException {
        var sessions = gameSessions.get(game.gameID());
        if (sessions == null) {
            return;
        }
        for (var session : sessions) {
            if (!Objects.equals(session.session(), excludedSession)) {
                String response = new Gson().toJson(new LoadGameMessage(game));
                session.session().getRemote().sendString(response);
            }
        }
    }
}
