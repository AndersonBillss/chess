package handlers.websocket;

import com.google.gson.Gson;
import dataaccess.GameDAO;
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
    public final Map<Integer, ArrayList<Session>> gameSessions;

    public SessionManager() {
        this.gameSessions = new HashMap<>();
    }

    public void addSession(int gameId, Session session) {
        var sessions = gameSessions.get(gameId);
        if (sessions == null) {
            sessions = new ArrayList<>();
            gameSessions.put(gameId, sessions);
        }
        sessions.add(session);
    }

    public void removeSession(int gameId, Session session) {
        var sessions = gameSessions.get(gameId);
        if (sessions == null) {
            return;
        }
        sessions.remove(session);
    }

    public void broadcast(int gameId, Session excludedSession, String message) throws IOException {
        var sessions = gameSessions.get(gameId);
        if (sessions == null) {
            return;
        }
        for (var session : sessions) {
            if (!Objects.equals(session, excludedSession)) {
                String response = new Gson().toJson(new NotificationMessage(message));
                session.getRemote().sendString(response);
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
            if (!Objects.equals(session, excludedSession)) {
                String response = new Gson().toJson(new LoadGameMessage(game));
                session.getRemote().sendString(response);
            }
        }
    }
}
