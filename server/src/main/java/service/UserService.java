package service;

import dataaccess.*;
import dto.RegisterRequest;
import dto.RegisterResult;
import model.UserData;

import javax.xml.crypto.Data;

public class UserService {
    private AuthDAO authDao;
    private UserDAO userDAO;

    public UserService() {
        this.authDao = new MemoryAuthDAO();
        this.userDAO = new MemoryUserDAO();
    }

    public RegisterResult register(RegisterRequest req) throws AlreadyTakenException, DataAccessException {
        UserData newUser = new UserData(req.username(), req.password(), req.email());
        var existingUser = userDAO.getUser(req.username());
        if (existingUser != null) {
            throw new AlreadyTakenException("Error: username already taken");
        }
        userDAO.createUser(newUser);
        return new RegisterResult("test", "This is a test token");
    }
}
