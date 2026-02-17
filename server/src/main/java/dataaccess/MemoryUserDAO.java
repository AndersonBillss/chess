package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    private HashMap<String, UserData> users;

    @Override
    public void createUser(UserData u) throws DataAccessException {
        users.put(u.username(), u);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        var result = users.get(username);
        if(result == null) {
            throw new DataAccessException("Username doesn't exist");
        }
        return result;
    }
}
