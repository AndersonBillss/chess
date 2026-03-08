package service;

import chess.*;
import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import static java.sql.Types.NULL;
import static org.junit.jupiter.api.Assertions.*;

public class MySqlDAOTests {
    private static MySqlAuthDAO authDao;
    private static MySqlUserDAO userDao;
    private static MySqlGameDAO gameDao;

    interface SelectCB {
        void onSelect(ResultSet res) throws SQLException;
    }

    static void executeSelect(String query, SelectCB cb, Object... params) throws DataAccessException, SQLException {
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
                var resultSet = ps.executeQuery();
                resultSet.next();
                cb.onSelect(resultSet);
            }
        }
    }

    static void executeUpdate(String query, Object... params) throws DataAccessException, SQLException {
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
    static void startUp() throws DataAccessException, SQLException {
        DatabaseManager.createDatabase();
        authDao = new MySqlAuthDAO();
        userDao = new MySqlUserDAO();
        gameDao = new MySqlGameDAO();
        executeUpdate("TRUNCATE TABLE games;");
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

    @Test
    void userDaoClearTest() throws SQLException, DataAccessException {
        executeUpdate("INSERT INTO users (username, password, email) VALUES (?, ?, ?);",
                "1", "2", "3");
        executeUpdate("INSERT INTO users (username, password, email) VALUES (?, ?, ?);",
                "4", "5", "6");
        executeUpdate("INSERT INTO users (username, password, email) VALUES (?, ?, ?);",
                "7", "8", "9");

        userDao.clear();
        executeSelect("SELECT username FROM users", res -> {
            assertFalse(res.next());
        });
    }

    @Test
    void authDaoCreateAuthTest() throws DataAccessException, SQLException {
        AuthData newAuth = new AuthData("TEST", "Batman");
        authDao.createAuth(newAuth);
        executeSelect(
                "SELECT authToken, username FROM auth;",
                res -> {
                    var resultAuthToken = res.getString(1);
                    var resultUsername = res.getString(2);
                    assertEquals(newAuth.authToken(), resultAuthToken);
                    assertEquals(newAuth.username(), resultUsername);
                }
        );
    }

    @Test
    void authDaoCreateNullAuthTest() {
        AuthData newAuth = new AuthData(null, "Batman");
        assertThrows(DataAccessException.class, () -> authDao.createAuth(newAuth));
    }

    @Test
    void authDaoGetAuthTest() throws DataAccessException, SQLException {
        executeUpdate("INSERT INTO auth (authToken, username) VALUES (?, ?);",
                "1", "Gandalf");
        assertEquals("Gandalf", authDao.getAuth("1").username());
    }

    @Test
    void authDaoGetNullAuthTest() {
        assertThrows(DataAccessException.class, () -> authDao.getAuth(null));
    }

    @Test
    void authDaoDeleteAuthTest() throws SQLException, DataAccessException {
        executeUpdate("INSERT INTO auth (authToken, username) VALUES (?, ?);",
                "1", "Gandalf");
        executeUpdate("INSERT INTO auth (authToken, username) VALUES (?, ?);",
                "2", "Frodo");
        authDao.deleteAuth("1");
        executeSelect("SELECT username from auth", (res -> {
            var resultUsername = res.getString(1);
            assertEquals("Frodo", resultUsername);
            assertFalse(res.next());
        }));
    }

    @Test
    void authDaoDeleteNullTokenTest() {
        assertThrows(DataAccessException.class, () -> authDao.deleteAuth(null));
    }

    @Test
    void authDaoClearTest() throws SQLException, DataAccessException {
        executeUpdate("INSERT INTO auth (authToken, username) VALUES (?, ?);",
                "1", "Gandalf");
        executeUpdate("INSERT INTO auth (authToken, username) VALUES (?, ?);",
                "2", "Frodo");
        executeUpdate("INSERT INTO auth (authToken, username) VALUES (?, ?);",
                "2", "Sam");

        authDao.clear();
        executeSelect("SELECT username FROM auth", res -> {
            assertFalse(res.next());
        });
    }

    @Test
    void gameDaoCreateGameTest() throws DataAccessException, SQLException {
        GameData game = new GameData(
                0, null, null, "TestGame", new ChessGame());
        gameDao.createGame(game);
        executeSelect("SELECT gameName FROM games", res -> {
            var resultGameName = res.getString(1);
            assertEquals(game.gameName(), resultGameName);
            assertFalse(res.next());
        });
    }

    @Test
    void gameDaoCreateGameNullGameNameTest() {
        GameData game = new GameData(
                0, null, null, null, new ChessGame());
        assertThrows(DataAccessException.class, () -> gameDao.createGame(game));
    }

    @Test
    void gameDaoGetGameTest() throws SQLException, DataAccessException {
        var gson = new Gson();
        ChessGame game = new ChessGame();
        String gameJson = gson.toJson(game);
        executeUpdate(
                "INSERT INTO games" +
                        "(id, whiteUsername, blackUsername, gameName, game) " +
                        "VALUES (?, ?, ?, ?, ?);",
                1, null, null, "TestGame", gameJson);

        assertEquals("TestGame", gameDao.getGame(1).gameName());
    }

    @Test
    void gameDaoGetGameNegativeIdTest() throws SQLException, DataAccessException {
        var gson = new Gson();
        ChessGame game = new ChessGame();
        String gameJson = gson.toJson(game);
        executeUpdate(
                "INSERT INTO games" +
                        "(id, whiteUsername, blackUsername, gameName, game) " +
                        "VALUES (?, ?, ?, ?, ?);",
                1, null, null, "TestGame", gameJson);

        assertEquals("TestGame", gameDao.getGame(1).gameName());
        assertThrows(DataAccessException.class, () -> gameDao.getGame(-1));
    }

    @Test
    void gameDaoEditGameTest() throws SQLException, DataAccessException {
        var gson = new Gson();
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(
                0,
                null,
                null,
                "TestGame",
                game);
        String gameJson = gson.toJson(game);
        executeUpdate(
                "INSERT INTO games" +
                        "(whiteUsername, blackUsername, gameName, game) " +
                        "VALUES (?, ?, ?, ?);",
                gameData.whiteUsername(),
                gameData.blackUsername(),
                "TestGame",
                gameJson);

        GameData newGameData = new GameData(
                1,
                "Player1",
                "Player2",
                "TestGame1",
                game
        );

        gameDao.editGame(newGameData);

        executeSelect(
                "SELECT whiteUsername, blackUsername, gameName FROM games",
                res -> {
                    var whiteUsername = res.getString(1);
                    var blackUsername = res.getString(2);
                    var gameName = res.getString(3);

                    assertEquals(newGameData.whiteUsername(), whiteUsername);
                    assertEquals(newGameData.blackUsername(), blackUsername);
                    assertEquals(newGameData.gameName(), gameName);
                }
        );
    }

    @Test
    void gameDaoEditGameState() throws SQLException, DataAccessException, InvalidMoveException {
        var gson = new Gson();
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(
                0,
                null,
                null,
                "TestGame",
                game);
        String gameJson = gson.toJson(game);
        executeUpdate(
                "INSERT INTO games" +
                        "(id, whiteUsername, blackUsername, gameName, game) " +
                        "VALUES (?, ?, ?, ?, ?);",
                1,
                gameData.whiteUsername(),
                gameData.blackUsername(),
                "TestGame",
                gameJson);

        ChessBoard board = new ChessBoard();
        board.addPiece(
                new ChessPosition(1, 1),
                new ChessPiece(
                        ChessGame.TeamColor.BLACK,
                        ChessPiece.PieceType.PAWN
                )
        );
        game.setBoard(board);
        GameData newGameData = new GameData(
                1,
                gameData.whiteUsername(),
                gameData.blackUsername(),
                gameData.gameName(),
                game
        );

        gameDao.editGame(newGameData);

        executeSelect(
                "SELECT game FROM games WHERE id = ?",
                res -> {
                    var editedGameJson = res.getString(1);
                    ChessGame editedGame = gson.fromJson(editedGameJson, ChessGame.class);
                    assertNotNull(editedGame.getBoard().getPiece(
                            new ChessPosition(1, 1)
                    ));
                },
                1
        );
    }

    @Test
    void gameDaoEditGameNullGameName() throws SQLException, DataAccessException {
        var gson = new Gson();
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(
                0,
                null,
                null,
                "TestGame",
                game);
        String gameJson = gson.toJson(game);
        executeUpdate(
                "INSERT INTO games" +
                        "(id, whiteUsername, blackUsername, gameName, game) " +
                        "VALUES (?, ?, ?, ?, ?);",
                1,
                gameData.whiteUsername(),
                gameData.blackUsername(),
                "TestGame",
                gameJson);

        GameData newGameData = new GameData(
                1,
                null,
                null,
                null,
                game
        );

        assertThrows(DataAccessException.class, () -> gameDao.editGame(newGameData));
    }

    @Test
    void gameDaoGetGamesTest() throws SQLException, DataAccessException {
        var gson = new Gson();
        ChessGame game = new ChessGame();
        String gameJson = gson.toJson(game);

        executeUpdate(
                "INSERT INTO games" +
                        "(whiteUsername, blackUsername, gameName, game) " +
                        "VALUES (?, ?, ?, ?);",
                null, null, "TestGame", gameJson);

        executeUpdate(
                "INSERT INTO games" +
                        "(whiteUsername, blackUsername, gameName, game) " +
                        "VALUES (?, ?, ?, ?);",
                null, null, "TestGame2", gameJson);

        executeUpdate(
                "INSERT INTO games" +
                        "(whiteUsername, blackUsername, gameName, game) " +
                        "VALUES (?, ?, ?, ?);",
                null, null, "TestGame3", gameJson);

        Collection<GameData> allGames = gameDao.getGames();
        assertEquals(3, allGames.size());
    }

    @Test
    void gameDaoClearGamesTest() throws SQLException, DataAccessException {
        var gson = new Gson();
        ChessGame game = new ChessGame();
        String gameJson = gson.toJson(game);

        executeUpdate(
                "INSERT INTO games" +
                        "(whiteUsername, blackUsername, gameName, game) " +
                        "VALUES (?, ?, ?, ?);",
                null, null, "TestGame", gameJson);

        executeUpdate(
                "INSERT INTO games" +
                        "(whiteUsername, blackUsername, gameName, game) " +
                        "VALUES (?, ?, ?, ?);",
                null, null, "TestGame2", gameJson);

        executeUpdate(
                "INSERT INTO games" +
                        "(whiteUsername, blackUsername, gameName, game) " +
                        "VALUES (?, ?, ?, ?);",
                null, null, "TestGame3", gameJson);

        gameDao.clear();

        executeSelect("SELECT gameName FROM games", res -> {
            assertFalse(res.next());
        });
    }
}
