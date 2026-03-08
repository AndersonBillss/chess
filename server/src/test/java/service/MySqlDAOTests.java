package service;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Types.NULL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MySqlDAOTests {
    interface SelectCB {
        void onSelect(ResultSet res) throws SQLException;
    }

    interface UpdateCB {
        void onUpdate(ResultSet res) throws SQLException;
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

    void executeUpdate(String query, Object... params) throws DataAccessException, SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) {
                        ps.setString(i + 1, p);
                    } else if (param instanceof Integer p) {
                        ps.setInt(i + 1, p);
                    } else if (param == null) {
                        ps.setNull(i + 1, NULL);
                    }
                }
                ps.executeUpdate();
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
        UserData newUser = new UserData("LukeSkywalker", "superSecure", "luke@test");
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

    @Test
    void userDaoCreateUserNullUsernameTest() {
        UserData newUser = new UserData(null, "superSecure", "luke@test");
        assertThrows(DataAccessException.class, () -> userDao.createUser(newUser));
    }

    @Test
    void userDaoGetUserTest() throws SQLException, DataAccessException {
        UserData user = new UserData("DarthVader", "password", "DVader@test");
        executeUpdate("INSERT INTO users (username, password, email) VALUES (?, ?, ?);",
                user.username(), user.password(), user.email());
        UserData retrievedUser = userDao.getUser(user.username());
        assertEquals(user.username(), retrievedUser.username());
        assertEquals(user.password(), retrievedUser.password());
        assertEquals(user.email(), retrievedUser.email());
    }

    @Test
    void userDaoGetUserNullUsernameTest() {
        assertThrows(DataAccessException.class, () -> userDao.getUser(null));
    }
}
