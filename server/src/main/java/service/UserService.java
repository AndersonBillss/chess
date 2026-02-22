package service;

import dataaccess.*;
import dto.RegisterRequest;
import dto.RegisterResult;
import model.AuthData;
import model.UserData;

public class UserService {
    private final AuthDAO authDao;
    private final UserDAO userDAO;

    public UserService(AuthDAO authDao, UserDAO userDao) {
        this.authDao = authDao;
        this.userDAO = userDao;
    }

    public RegisterResult register(RegisterRequest req) throws AlreadyTakenException, DataAccessException {
        UserData newUser = new UserData(req.username(), req.password(), req.email());
        var existingUser = userDAO.getUser(req.username());
        if (existingUser != null) {
            throw new AlreadyTakenException();
        }
        userDAO.createUser(newUser);
        String token = ServiceUtils.generateToken();
        authDao.createAuth(new AuthData(token, newUser.username()));
        return new RegisterResult(token, newUser.username());
    }
}
