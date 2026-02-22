package server;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import handlers.AuthHandler;
import handlers.GameHandler;
import handlers.HandlerUtils;
import handlers.UserHandler;
import io.javalin.*;
import service.AuthService;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        var userDao = new MemoryUserDAO();
        var authDao = new MemoryAuthDAO();
        var gameDao = new MemoryGameDAO();

        var userService = new UserService(authDao, userDao);
        var authService = new AuthService(authDao, userDao);
        var gameService = new GameService(authDao, gameDao);

        var userHandler = new UserHandler(userService);
        var authHandler = new AuthHandler(authService);
        var gameHandler = new GameHandler(gameService);

        // Register your endpoints and exception handlers here.
        javalin
                .post("/user", HandlerUtils.handleErr(userHandler::register))
                .post("/session", HandlerUtils.handleErr(authHandler::login))
                .delete("/session", HandlerUtils.handleErr(authHandler::logout))
                .get("/game", HandlerUtils.handleErr(gameHandler::listGames))
                .post("/game", HandlerUtils.handleErr(gameHandler::createGame))
                .put("/game", HandlerUtils.handleErr(gameHandler::joinGame));
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
