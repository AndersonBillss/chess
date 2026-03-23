import chess.ChessGame;
import dataaccess.*;
import dto.CreateGameRequest;
import dto.JoinGameRequest;
import dto.LoginRequest;
import dto.RegisterRequest;
import exception.ResponseException;
import model.GameData;
import model.UserData;
import server.Server;
import server.ServerFacade;
import org.junit.jupiter.api.*;


public class ServerFacadeTests {
    private static MySqlAuthDAO authDao;
    private static MySqlUserDAO userDao;
    private static MySqlGameDAO gameDao;

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() throws DataAccessException {
        server = new Server();
        authDao = new MySqlAuthDAO();
        userDao = new MySqlUserDAO();
        gameDao = new MySqlGameDAO();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade(String.format("http://localhost:%d", port));
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void beforeEach() throws DataAccessException {
        authDao.clear();
        userDao.clear();
        gameDao.clear();
    }

    @Test
    void registerSuccess() throws ResponseException {
        RegisterRequest req = new RegisterRequest("Test", "Test", "Test");
        var result = serverFacade.register(req);
        Assertions.assertEquals(result.username(), "Test");
        Assertions.assertNotNull(result.authToken());
    }

    @Test
    void registerFailure() throws ResponseException {
        RegisterRequest req = new RegisterRequest("Test", "Test", "Test");
        serverFacade.register(req);
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.register(req));
    }

    @Test
    void loginSuccess() throws ResponseException, DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("Test", "Password", "Test");
        serverFacade.register(registerRequest);
        serverFacade.logout();
        userDao.createUser(new UserData("Test", "Password", "Test"));
        LoginRequest loginRequest = new LoginRequest("Test", "Password");
        var result = serverFacade.login(loginRequest);
        Assertions.assertEquals(result.username(), "Test");
        Assertions.assertNotNull(result.authToken());
    }

    @Test
    void loginFailure() throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("Test", "Password", "Test");
        serverFacade.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("Test", "Password");
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.login(loginRequest));
    }

    @Test
    void logoutSuccess() throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("Test", "Password", "Test");
        serverFacade.register(registerRequest);
        Assertions.assertDoesNotThrow(() -> serverFacade.logout());
    }

    @Test
    void logoutFailure() {
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.logout());
    }

    @Test
    void listGamesSuccess() throws DataAccessException, ResponseException {
        var game1 = new GameData(1,
                null,
                null,
                "Test Game 1",
                new ChessGame());
        gameDao.createGame(game1);
        var game2 = new GameData(1,
                null,
                null,
                "Test Game 2",
                new ChessGame());
        gameDao.createGame(game2);

        RegisterRequest registerRequest = new RegisterRequest("Test", "Password", "Test");
        serverFacade.register(registerRequest);

        var games = serverFacade.listGames();
        Assertions.assertEquals(2, games.games().size());
    }

    @Test
    void listGamesFailure() {
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.listGames());
    }

    @Test
    void createGameTest() throws ResponseException, DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("Test", "Password", "Test");
        serverFacade.register(registerRequest);

        var createGameRequest = new CreateGameRequest("MyGame");
        var res = serverFacade.createGame(createGameRequest);

        var game = gameDao.getGame(res.gameID());
        Assertions.assertEquals("MyGame", game.gameName());
    }

    @Test
    void createGameFailure() throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("Test", "Password", "Test");
        serverFacade.register(registerRequest);

        var createGameRequest = new CreateGameRequest(null);
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.createGame(createGameRequest));
    }

    @Test
    void joinGameSuccess() throws DataAccessException, ResponseException {
        var game1 = new GameData(1,
                null,
                null,
                "Test Game 1",
                new ChessGame());
        int gameId = gameDao.createGame(game1);

        RegisterRequest registerRequest = new RegisterRequest("Test", "Password", "Test");
        serverFacade.register(registerRequest);

        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", gameId);
        serverFacade.joinGame(joinGameRequest);

        var joinedGame = gameDao.getGame(gameId);
        Assertions.assertEquals("Test", joinedGame.whiteUsername());
    }

    @Test
    void joinGameFailure() throws ResponseException, DataAccessException {
        var game1 = new GameData(1,
                null,
                null,
                "Test Game 1",
                new ChessGame());
        int gameId = gameDao.createGame(game1);

        RegisterRequest registerRequest = new RegisterRequest("Test", "Password", "Test");
        serverFacade.register(registerRequest);

        JoinGameRequest joinGameRequest = new JoinGameRequest("Invalid Color", gameId);
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.joinGame(joinGameRequest));
    }
}
