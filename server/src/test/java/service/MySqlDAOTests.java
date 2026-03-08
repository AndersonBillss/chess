package service;

import dataaccess.DataAccessException;
import dataaccess.MySqlAuthDAO;
import dataaccess.MySqlGameDAO;
import dataaccess.MySqlUserDAO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public class MySqlDAOTests {
    private static MySqlAuthDAO authDao;
    private static MySqlUserDAO userDao;
    private static MySqlGameDAO gameDao;

    @BeforeAll
    static void startUp() throws DataAccessException {
        authDao = new MySqlAuthDAO();
        userDao = new MySqlUserDAO();
        gameDao = new MySqlGameDAO();
    }

    @BeforeEach
    void init() throws DataAccessException {
        authDao.clear();
        userDao.clear();
        gameDao.clear();
    }
}
