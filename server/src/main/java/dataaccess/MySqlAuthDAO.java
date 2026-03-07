package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.SQLException;

public class MySqlAuthDAO implements AuthDAO {
    public MySqlAuthDAO() throws DataAccessException {
        createTable();
    }

    @Override
    public void createAuth(AuthData data) throws DataAccessException {

    }

    @Override
    public AuthData getAuth(String token) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String token) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {

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
