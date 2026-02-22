package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    private final HashMap<String, UserData> users;

    public MemoryUserDAO() {
        users = new HashMap<>();
    }

    @Override
    public void createUser(UserData u) throws DataAccessException {
        boolean userFound = users.get(u.username()) != null;
        if(userFound) {
            throw new DataAccessException("User already exists");
        }
        users.put(u.username(), u);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        var result = users.get(username);
        if (result == null) {
            throw new DataAccessException("Username doesn't exist");
        }
        return result;
    }
}
