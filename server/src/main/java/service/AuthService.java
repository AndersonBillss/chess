package service;

import dataaccess.*;
import dto.LoginRequest;
import dto.LoginResult;
import dto.LogoutRequest;
import model.AuthData;

public class AuthService {
    private final AuthDAO authDao;
    private final UserDAO userDAO;

    public AuthService() {
        this.authDao = new MemoryAuthDAO();
        this.userDAO = new MemoryUserDAO();
    }

    public LoginResult login(LoginRequest req) throws NotFoundException, DataAccessException {
        var existingUser = userDAO.getUser(req.username());
        if (existingUser == null) {
            throw new NotFoundException("Error: user not found");
        }
        String token = ServiceUtils.generateToken();
        authDao.createAuth(new AuthData(token, req.username()));
        return new LoginResult(token, req.username());
    }

    public void logout(LogoutRequest req) throws NotFoundException, DataAccessException {
        var existingSession = authDao.getAuth(req.authToken());
        if (existingSession == null) {
            throw new NotFoundException("Error: user not found");
        }
        authDao.deleteAuth(req.authToken());
    }
}
