package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dto.*;
import io.javalin.http.Context;
import service.AlreadyTakenException;
import service.GameService;
import service.NotFoundException;
import service.UnauthorizedException;

public class GameHandler {
    private final GameService service;

    public GameHandler(GameService service) {
        this.service = service;
    }

    public void listGames(Context ctx)
            throws DataAccessException, UnauthorizedException {
        var gson = new Gson();
        var authToken = ctx.header("Authorization");
        ListGamesResult res = service.listGames(authToken);
        ctx.json(gson.toJson(res));
    }

    public void createGame(Context ctx)
            throws DataAccessException, UnauthorizedException {
        var gson = new Gson();
        CreateGameRequest req = gson.fromJson(ctx.body(), CreateGameRequest.class);
        var authToken = ctx.header("Authorization");
        CreateGameResult res = service.createGame(req, authToken);
        ctx.json(gson.toJson(res));
    }

    public void joinGame(Context ctx)
            throws DataAccessException, UnauthorizedException, NotFoundException, AlreadyTakenException {
        var gson = new Gson();
        JoinGameRequest req = gson.fromJson(ctx.body(), JoinGameRequest.class);
        var authToken = ctx.header("Authorization");
        service.joinGame(req, authToken);
        ctx.status(200);

    }
}
