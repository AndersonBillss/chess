package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySqlUserDAO implements UserDAO {
    public MySqlUserDAO() throws DataAccessException {
        createTable();
    }

    @Override
    public void createUser(UserData u) throws DataAccessException {
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?);";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, u.username());
                ps.setString(2, u.password());
                ps.setString(3, u.email());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    String.format("Unable to create user: %s%n", e.getMessage())
            );
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        var statement = "SELECT username, password, email FROM users WHERE username=?;";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                var resultSet = ps.executeQuery();
                if (!resultSet.next()) {
                    return null;
                }
                var resultUsername = resultSet.getString(1);
                var resultPassword = resultSet.getString(2);
                var resultEmail = resultSet.getString(3);
                return new UserData(resultUsername, resultPassword, resultEmail);
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    String.format("Unable to get user: %s%n", e.getMessage())
            );
        }
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "DELETE FROM users;";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    String.format("Unable to get user: %s%n", e.getMessage())
            );
        }
    }

    private void createTable() throws DataAccessException {
        final String createStatement = """
                CREATE TABLE IF NOT EXISTS users (
                  `id` int NOT NULL AUTO_INCREMENT,
                  `username` varchar(256) NOT NULL,
                  `password` varchar(256) NOT NULL,
                  `email` varchar(256) NOT NULL,
                  PRIMARY KEY (`id`)
                );
                """;

        try (Connection conn = DatabaseManager.getConnection()) {
            var preparedStatement = conn.prepareStatement(createStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(
                    String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
