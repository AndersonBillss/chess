package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class MySqlGameDAO implements GameDAO {
    public MySqlGameDAO() throws DataAccessException {
        createTable();
    }

    @Override
    public void createGame(GameData data) throws DataAccessException {
        var statement =
                "INSERT INTO game " +
                        "(id, whiteUsername, blackUsername, gameName, game) " +
                        "VALUES (?, ?, ?, ?, ?);";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, data.gameID());
                ps.setString(2, data.whiteUsername());
                ps.setString(3, data.blackUsername());
                ps.setString(4, data.gameName());
                var gson = new Gson();
                ps.setString(5, gson.toJson(data.game()));
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    String.format("Unable to create game: %s%n", e.getMessage())
            );
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        var statement = "SELECT " +
                "id, whiteUsername, blackUsername, gameName, game " +
                "FROM games WHERE gameID=?;";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                var resultSet = ps.executeQuery();
                if (!resultSet.next()) {
                    return null;
                }
                var resultGameId = resultSet.getInt(1);
                var resultWhiteUsername = resultSet.getString(2);
                var resultBlackUsername = resultSet.getString(3);
                var resultGameName = resultSet.getString(4);
                var resultGameJson = resultSet.getString(5);
                var gson = new Gson();
                ChessGame resultGame = gson.fromJson(resultGameJson, ChessGame.class);
                return new GameData(
                        resultGameId,
                        resultWhiteUsername,
                        resultBlackUsername,
                        resultGameName,
                        resultGame
                );
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    String.format("Unable to get game: %s%n", e.getMessage())
            );
        }
    }

    @Override
    public void editGame(GameData data) throws DataAccessException {

    }

    @Override
    public Collection<GameData> getGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "DELETE FROM games;";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(
                    String.format("Unable to delete games: %s%n", e.getMessage())
            );
        }

    }

    private void createTable() throws DataAccessException {
        final String createStatement = """
                CREATE TABLE IF NOT EXISTS game (
                  `id` int NOT NULL,
                  `whiteUsername` varchar(256) default NULL,
                  `blackUsername` varchar(256) default NULL,
                  `gameName` varchar(256) NOT NULL,
                  `game` text NOT NULL,
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
