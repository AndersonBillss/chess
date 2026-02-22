package service;

import dataaccess.*;
import dto.LoginRequest;
import dto.LoginResult;
import model.AuthData;

public class AuthService {
    private final AuthDAO authDao;
    private final UserDAO userDAO;

    public AuthService(AuthDAO authDao, UserDAO userDao) {
        this.authDao = authDao;
        this.userDAO = userDao;
    }

    public LoginResult login(LoginRequest req)
            throws NotFoundException, DataAccessException, UnauthorizedException {
        var existingUser = userDAO.getUser(req.username());
        if (existingUser == null) {
            throw new NotFoundException();
        }
        if (existingUser.password() != req.password()) {
            throw new UnauthorizedException();
        }
        String token = ServiceUtils.generateToken();
        authDao.createAuth(new AuthData(token, req.username()));
        return new LoginResult(req.username(), token);
    }

    public void logout(String authToken) throws NotFoundException, DataAccessException {
        var existingSession = authDao.getAuth(authToken);
        if (existingSession == null) {
            throw new NotFoundException();
        }
        authDao.deleteAuth(authToken);
    }
}
