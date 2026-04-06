package server;

import dataaccess.*;
import handlers.*;
import io.javalin.*;
import handlers.websocket.WebSocketHandler;
import service.AuthService;
import service.DBService;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            System.err.printf("Error connecting to database: %s%n", e.getMessage());
            System.exit(1);
        }

        AuthDAO authDao;
        try {
            authDao = new MySqlAuthDAO();
        } catch (DataAccessException e) {
            System.err.printf("Error creating auth DAO: %s%n", e.getMessage());
            System.exit(1);
            return;
        }

        UserDAO userDao;
        try {
            userDao = new MySqlUserDAO();
        } catch (DataAccessException e) {
            System.err.printf("Error creating user DAO: %s%n", e.getMessage());
            System.exit(1);
            return;
        }

        GameDAO gameDao;
        try {
            gameDao = new MySqlGameDAO();
        } catch (DataAccessException e) {
            System.err.printf("Error creating game DAO: %s%n", e.getMessage());
            System.exit(1);
            return;
        }

        var userService = new UserService(authDao, userDao);
        var authService = new AuthService(authDao, userDao);
        var gameService = new GameService(authDao, gameDao);
        var dbService = new DBService(authDao, userDao, gameDao);

        var userHandler = new UserHandler(userService);
        var authHandler = new AuthHandler(authService);
        var gameHandler = new GameHandler(gameService);
        var dbHandler = new DBHandler(dbService);

        var webSocketHandler = new WebSocketHandler(gameDao, userDao, authDao);

        // Register your endpoints and exception handlers here.
        javalin
                .delete("/db", HandlerUtils.handleErr(dbHandler::clear))
                .post("/user", HandlerUtils.handleErr(userHandler::register))
                .post("/session", HandlerUtils.handleErr(authHandler::login))
                .delete("/session", HandlerUtils.handleErr(authHandler::logout))
                .get("/game", HandlerUtils.handleErr(gameHandler::listGames))
                .post("/game", HandlerUtils.handleErr(gameHandler::createGame))
                .put("/game", HandlerUtils.handleErr(gameHandler::joinGame))
                .ws("/ws", ws -> {
                    ws.onConnect(webSocketHandler);
                    ws.onMessage(HandlerUtils.handleErrWebsocket(webSocketHandler));
                    ws.onClose(webSocketHandler);
                });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}