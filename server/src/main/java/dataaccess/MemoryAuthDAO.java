package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryAuthDAO {
    private int tokenCount = 0;
    private HashMap<String, String> authSessions;

    public void createAuth(UserData userData) {
        String key = generateToken();
        authSessions.put(key, userData.username());
    }

    private String generateToken() {
        this.tokenCount++;
        return Integer.toString(tokenCount);
    }
}
