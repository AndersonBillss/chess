package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;

public class MySqlUserDAO implements UserDAO {
    public MySqlUserDAO() throws DataAccessException {
        createTable();
    }

    @Override
    public void createUser(UserData u) throws DataAccessException {
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void clear() throws DataAccessException {

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
