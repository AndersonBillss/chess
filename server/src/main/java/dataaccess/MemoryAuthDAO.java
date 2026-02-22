package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    private HashMap<String, AuthData> authSessions;

    public MemoryAuthDAO() {
        authSessions = new HashMap<>();
    }

    public void createAuth(AuthData data) {
        authSessions.put(data.authToken(), data);
    }

    public AuthData getAuth(String token) {
        return authSessions.get(token);
    }

    public void deleteAuth(String token) {
        authSessions.remove(token);
    }

    public void clear() {
        authSessions = new HashMap<>();
    }
}
