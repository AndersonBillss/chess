package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    private int tokenCount = 0;
    private final HashMap<String, String> authSessions;

    public MemoryAuthDAO() {
        authSessions = new HashMap<>();
    }

    public void createAuth(UserData userData) {
        String key = generateToken();
        authSessions.put(key, userData.username());
    }

    private String generateToken() {
        this.tokenCount++;
        return Integer.toString(tokenCount);
    }
}
