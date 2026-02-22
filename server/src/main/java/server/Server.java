package server;

import handlers.AuthHandler;
import handlers.HandlerUtils;
import handlers.UserHandler;
import io.javalin.*;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        var userHandler = new UserHandler();
        var authHandler = new AuthHandler();
        // Register your endpoints and exception handlers here.
        javalin
                .post("/user", HandlerUtils.handleErr(userHandler::register))
                .post("/session", HandlerUtils.handleErr(authHandler::login))
                .delete("/session", HandlerUtils.handleErr(authHandler::logout));
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
