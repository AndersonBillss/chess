package handlers.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
            if (session != excludedSession) {
                session.getRemote().sendString(new Gson().toJson(new NotificationMessage(message)));
            }
        }
    }
}
