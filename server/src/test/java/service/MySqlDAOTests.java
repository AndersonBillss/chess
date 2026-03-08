package service;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.transform.Result;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MySqlDAOTests {
    interface SelectCB {
        void onSelect(ResultSet res) throws SQLException;
    }

    private static MySqlAuthDAO authDao;
    private static MySqlUserDAO userDao;
    private static MySqlGameDAO gameDao;

    void executeSelect(String query, SelectCB cb) throws DataAccessException, SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                var resultSet = ps.executeQuery();
                resultSet.next();
                cb.onSelect(resultSet);
            }
        }
    }

    @BeforeAll
    static void startUp() throws DataAccessException {
        DatabaseManager.createDatabase();
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

    @Test
    void userDaoCreateUserTest() throws DataAccessException, SQLException {
        UserData newUser = new UserData("Luke", "superSecure", "luke@test");
        userDao.createUser(newUser);
        executeSelect(
                "SELECT username, password, email FROM users;",
                res -> {
                    var resultUsername = res.getString(1);
                    var resultPassword = res.getString(2);
                    var resultEmail = res.getString(3);
                    assertEquals(newUser.username(), resultUsername);
                    assertEquals(newUser.password(), resultPassword);
                    assertEquals(newUser.email(), resultEmail);
                }
        );
    }
}
