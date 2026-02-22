package service;

import dataaccess.*;
import dto.LoginRequest;
import dto.LoginResult;
import model.AuthData;

import java.util.Objects;

public class AuthService {
    private final AuthDAO authDao;
    private final UserDAO userDAO;

    public AuthService(AuthDAO authDao, UserDAO userDao) {
        this.authDao = authDao;
        this.userDAO = userDao;
    }

    public LoginResult login(LoginRequest req, String authToken)
            throws DataAccessException, UnauthorizedException {
        var existingUser = userDAO.getUser(req.username());
        if (existingUser == null) {
            throw new UnauthorizedException();
        }
        var existingSession = authDao.getAuth(authToken);
        if (existingSession != null) {
            throw new UnauthorizedException();
        }
        if (!Objects.equals(existingUser.password(), req.password())) {
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
