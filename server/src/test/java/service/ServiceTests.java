package service;

import dataaccess.*;
import dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServiceTests {
    private AuthDAO authDao;
    private UserDAO userDao;
    private GameDAO gameDao;

    private UserService userService;
    private AuthService authService;
    private GameService gameService;
    private DBService dbService;

    @BeforeEach
    void init() {
        authDao = new MemoryAuthDAO();
        userDao = new MemoryUserDAO();
        gameDao = new MemoryGameDAO();

        userService = new UserService(authDao, userDao);
        authService = new AuthService(authDao, userDao);
        gameService = new GameService(authDao, gameDao);
        dbService = new DBService(authDao, userDao, gameDao);
    }

    @Test
    void registerSuccessTest()
            throws DataAccessException, AlreadyTakenException, BadRequestException {
        RegisterRequest req = new RegisterRequest("John", "123", "test@test");
        var res = userService.register(req);

        var user = userDao.getUser(res.username());
        assertEquals("John", user.username());

        var session = authDao.getAuth(res.authToken());
        assertEquals("John", session.username());
    }

    @Test
    void registerDuplicateNamesTest()
            throws DataAccessException, AlreadyTakenException, BadRequestException {
        RegisterRequest req = new RegisterRequest("John", "123", "test@test");
        userService.register(req);
        assertThrows(AlreadyTakenException.class, () -> userService.register(req));
    }

    @Test
    void logoutSuccessTest()
            throws AlreadyTakenException, DataAccessException, BadRequestException, UnauthorizedException {
        RegisterRequest req = new RegisterRequest("John", "123", "test@test");
        var res = userService.register(req);

        authService.logout(res.authToken());
        var auth = authDao.getAuth(res.authToken());
        assertNull(auth);
    }

    @Test
    void logoutTwiceTest()
            throws AlreadyTakenException, DataAccessException, BadRequestException, UnauthorizedException {
        RegisterRequest req = new RegisterRequest("John", "123", "test@test");
        var res = userService.register(req);

        authService.logout(res.authToken());
        assertThrows(UnauthorizedException.class, () -> authService.logout(res.authToken()));
    }

    @Test
    void loginSuccessTest()
            throws AlreadyTakenException, DataAccessException, UnauthorizedException, BadRequestException {
        RegisterRequest req = new RegisterRequest("John", "123", "test@test");
        var res = userService.register(req);
        authService.logout(res.authToken());

        LoginRequest loginRequest = new LoginRequest("John", "123");
        var loginResponse = authService.login(loginRequest, null);

        var session = authDao.getAuth(loginResponse.authToken());
        assertEquals("John", session.username());
    }

    @Test
    void loginIncorrectPasswordTest()
            throws AlreadyTakenException, DataAccessException, BadRequestException, UnauthorizedException {

        RegisterRequest req = new RegisterRequest("John", "123", "test@test");
        var res = userService.register(req);
        authService.logout(res.authToken());

        LoginRequest loginRequest = new LoginRequest("John", "1234");
        assertThrows(UnauthorizedException.class, () -> authService.login(loginRequest, null));
    }

    @Test
    void loginIncorrectUsernameTest()
            throws AlreadyTakenException, DataAccessException, BadRequestException, UnauthorizedException {

        RegisterRequest req = new RegisterRequest("John", "123", "test@test");
        var res = userService.register(req);
        authService.logout(res.authToken());

        LoginRequest loginRequest = new LoginRequest("Joe", "123");
        assertThrows(UnauthorizedException.class, () -> authService.login(loginRequest, null));
    }

    @Test
    void createGameSuccessTest()
            throws BadRequestException, AlreadyTakenException, DataAccessException, UnauthorizedException {
        RegisterRequest registerReq = new RegisterRequest("John", "123", "test@test");
        var authToken = userService.register(registerReq).authToken();

        String gameName = "TestGame";
        CreateGameRequest req = new CreateGameRequest(gameName);
        var res = gameService.createGame(req, authToken);

        var game = gameDao.getGame(res.gameID());
        assertEquals(gameName, game.gameName());
    }

    @Test
    void createGameNullNameTest()
            throws BadRequestException, DataAccessException, AlreadyTakenException {
        RegisterRequest registerReq = new RegisterRequest("John", "123", "test@test");
        var authToken = userService.register(registerReq).authToken();

        CreateGameRequest req = new CreateGameRequest(null);
        assertThrows(BadRequestException.class, () -> gameService.createGame(req, authToken));
    }

    @Test
    void listGameSuccessTest()
            throws BadRequestException, AlreadyTakenException, DataAccessException, UnauthorizedException {
        RegisterRequest registerReq = new RegisterRequest("John", "123", "test@test");
        var authToken = userService.register(registerReq).authToken();

        gameService.createGame(new CreateGameRequest("TestGame1"), authToken);
        gameService.createGame(new CreateGameRequest("TestGame2"), authToken);
        gameService.createGame(new CreateGameRequest("TestGame3"), authToken);
        gameService.createGame(new CreateGameRequest("TestGame4"), authToken);

        var allGames = gameService.listGames(authToken);
        assertEquals(4, allGames.games().size());
    }

    @Test
    void listGamesUnauthorizedTest() {
        assertThrows(UnauthorizedException.class, () -> gameService.listGames("Invalid Token"));
    }

    @Test
    void joinGameSuccessTest()
            throws BadRequestException, AlreadyTakenException, DataAccessException, UnauthorizedException {
        RegisterRequest registerReq = new RegisterRequest("John", "123", "test@test");
        var authToken = userService.register(registerReq).authToken();
        var game = gameService.createGame(new CreateGameRequest("TestGame1"), authToken);

        JoinGameRequest req = new JoinGameRequest("WHITE", game.gameID());
        gameService.joinGame(req, authToken);

        var resultGame = gameDao.getGame(game.gameID());
        assertEquals("John", resultGame.whiteUsername());
    }

    @Test
    void joinGameExistingPlayerTest()
            throws BadRequestException, AlreadyTakenException, DataAccessException, UnauthorizedException {
        RegisterRequest registerReq1 = new RegisterRequest("John", "123", "test@test");
        var p1AuthToken = userService.register(registerReq1).authToken();
        RegisterRequest registerReq2 = new RegisterRequest("Bob", "123", "test@test");
        var p2AuthToken = userService.register(registerReq2).authToken();
        var game = gameService.createGame(new CreateGameRequest("TestGame1"), p1AuthToken);

        JoinGameRequest req1 = new JoinGameRequest("WHITE", game.gameID());
        gameService.joinGame(req1, p1AuthToken);
        JoinGameRequest req2 = new JoinGameRequest("WHITE", game.gameID());
        assertThrows(AlreadyTakenException.class, () -> gameService.joinGame(req2, p2AuthToken));
    }

    @Test
    void clearDBTest()
            throws BadRequestException, AlreadyTakenException, DataAccessException, UnauthorizedException {
        RegisterRequest registerReq1 = new RegisterRequest("John", "123", "test@test");
        var p1AuthToken = userService.register(registerReq1).authToken();
        RegisterRequest registerReq2 = new RegisterRequest("Bob", "123", "test@test");
        var p2AuthToken = userService.register(registerReq2).authToken();
        var game = gameService.createGame(new CreateGameRequest("TestGame1"), p1AuthToken);

        JoinGameRequest req1 = new JoinGameRequest("WHITE", game.gameID());
        gameService.joinGame(req1, p1AuthToken);
        JoinGameRequest req2 = new JoinGameRequest("BLACK", game.gameID());
        gameService.joinGame(req2, p2AuthToken);

        dbService.clear();
        assertNull(authDao.getAuth(p1AuthToken));
        assertNull(authDao.getAuth(p2AuthToken));
        assertNull(userDao.getUser("John"));
        assertNull(userDao.getUser("Bob"));
        assertNull(gameDao.getGame(game.gameID()));
    }
}
