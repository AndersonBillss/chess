package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySqlAuthDAO implements AuthDAO {
    public MySqlAuthDAO() throws DataAccessException {
        createTable();
    }

    @Override
    public void createAuth(AuthData data) throws DataAccessException {
        var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?);";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, data.authToken());
                ps.setString(2, data.username());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    String.format("Unable to create auth: %s%n", e.getMessage())
            );
        }
    }

    @Override
    public AuthData getAuth(String token) throws DataAccessException {
        var statement = "SELECT authToken, username FROM auth WHERE authToken=?;";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, token);
                var resultSet = ps.executeQuery();
                if (!resultSet.next()) {
                    return null;
                }
                var resultAuthToken = resultSet.getString(1);
                var resultUsername = resultSet.getString(2);
                return new AuthData(resultAuthToken, resultUsername);
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    String.format("Unable to get auth: %s%n", e.getMessage())
            );
        }
    }

    @Override
    public void deleteAuth(String token) throws DataAccessException {
        var statement = "DELETE FROM auth where authToken=?;";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, token);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    String.format("Unable to delete auth: %s%n", e.getMessage())
            );
        }
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "DELETE FROM auth;";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    String.format("Unable to delete auth: %s%n", e.getMessage())
            );
        }
    }

    private void createTable() throws DataAccessException {
        final String createStatement = """
                CREATE TABLE IF NOT EXISTS auth (
                  `id` int NOT NULL AUTO_INCREMENT,
                  `authToken` varchar(256) NOT NULL,
                  `username` varchar(256) NOT NULL,
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
