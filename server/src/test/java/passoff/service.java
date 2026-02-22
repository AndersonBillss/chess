package passoff;

import dataaccess.*;
import dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.*;

import static org.junit.jupiter.api.Assertions.*;

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
        assertEquals("John", user.username());

        var session = authDao.getAuth(res.authToken());
        assertEquals("John", session.username());
    }

    @Test
    void registerDuplicateNamesTest()
            throws DataAccessException, AlreadyTakenException {
        RegisterRequest req = new RegisterRequest("John", "Doe", "test@test");
        userService.register(req);
        assertThrows(AlreadyTakenException.class, () -> userService.register(req));
    }

    @Test
    void logoutSuccessTest() throws AlreadyTakenException, DataAccessException, NotFoundException {
        RegisterRequest req = new RegisterRequest("John", "Doe", "test@test");
        var res = userService.register(req);

        authService.logout(res.authToken());
        var auth = authDao.getAuth(res.authToken());
        assertNull(auth);
    }

    @Test
    void logoutTwiceTest() throws AlreadyTakenException, DataAccessException, NotFoundException {
        RegisterRequest req = new RegisterRequest("John", "Doe", "test@test");
        var res = userService.register(req);

        authService.logout(res.authToken());
        assertThrows(NotFoundException.class, () -> authService.logout(res.authToken()));
    }
}
