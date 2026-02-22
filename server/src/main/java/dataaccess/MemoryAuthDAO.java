package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    private final HashMap<String, String> authSessions;

    public MemoryAuthDAO() {
        authSessions = new HashMap<>();
    }

    public void createAuth(AuthData data) {
        authSessions.put(data.authToken(), data.username());
    }
}
