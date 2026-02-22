package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class DBService {
    private final AuthDAO authDao;
    private final UserDAO userDao;
    private final GameDAO gameDao;

    public DBService(AuthDAO authDao, UserDAO userDao, GameDAO gameDao) {
        this.authDao = authDao;
        this.userDao = userDao;
        this.gameDao = gameDao;
    }

    public void clear() throws DataAccessException {
        authDao.clear();
        userDao.clear();
        gameDao.clear();
    }
}
