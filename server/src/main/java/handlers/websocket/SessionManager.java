package handlers.websocket;

import org.eclipse.jetty.websocket.api.Session;

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
        if(sessions == null) {
            sessions = new ArrayList<>();
            gameSessions.put(gameId, sessions);
        }
        sessions.add(session);
    }

    public void removeSession(int gameId, Session session) {
        var sessions = gameSessions.get(gameId);
        if(sessions == null) {
            return;
        }
        sessions.remove(session);
    }
}
