import com.mysql.cj.log.Log;
import dataaccess.DataAccessException;
import dataaccess.MySqlAuthDAO;
import dataaccess.MySqlGameDAO;
import dataaccess.MySqlUserDAO;
import dto.LoginRequest;
import dto.RegisterRequest;
import exception.ResponseException;
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
        var result = serverFacade.Register(req);
        Assertions.assertEquals(result.username(), "Test");
        Assertions.assertNotNull(result.authToken());
    }

    @Test
    void registerFailure() throws ResponseException {
        RegisterRequest req = new RegisterRequest("Test", "Test", "Test");
        serverFacade.Register(req);
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.Register(req));
    }

    @Test
    void loginSuccess() throws ResponseException, DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("Test", "Password", "Test");
        serverFacade.Register(registerRequest);
        serverFacade.Logout();
        userDao.createUser(new UserData("Test", "Password", "Test"));
        LoginRequest loginRequest = new LoginRequest("Test", "Password");
        var result = serverFacade.Login(loginRequest);
        Assertions.assertEquals(result.username(), "Test");
        Assertions.assertNotNull(result.authToken());
    }

    @Test
    void loginFailure() throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("Test", "Password", "Test");
        serverFacade.Register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("Test", "Password");
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.Login(loginRequest));
    }

    @Test
    void logoutSuccess() throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("Test", "Password", "Test");
        serverFacade.Register(registerRequest);
        Assertions.assertDoesNotThrow(() -> serverFacade.Logout());
    }

    @Test
    void logoutFailure() {
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.Logout());
    }
}
