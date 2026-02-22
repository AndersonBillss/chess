package passoff;

import dataaccess.*;
import dto.LoginRequest;
import dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.*;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class service {
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
            throws DataAccessException, AlreadyTakenException {
        RegisterRequest req = new RegisterRequest("John", "Doe", "test@test");
        var res = userService.register(req);

        var user = userDao.getUser(res.username());
        assertNotEquals(null, user);

        var session = authDao.getAuth(res.authToken());
        assertNotEquals(null, session);
    }
}
