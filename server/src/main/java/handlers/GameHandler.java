package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dto.CreateGameRequest;
import dto.CreateGameResult;
import dto.RegisterRequest;
import io.javalin.http.Context;
import service.GameService;
import service.UnauthorizedException;

public class GameHandler {
    private final GameService service;

    public GameHandler() {
        service = new GameService();
    }

    public void createGame(Context ctx)
            throws DataAccessException, UnauthorizedException {
        var gson = new Gson();
        CreateGameRequest req = gson.fromJson(ctx.body(), CreateGameRequest.class);
        var authToken = ctx.header("Authorization");
        CreateGameResult res = service.createGame(req, authToken);
        ctx.json(gson.toJson(res));
    }
}
