package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dto.LoginRequest;
import dto.LoginResult;
import io.javalin.http.Context;
import service.AuthService;
import service.NotFoundException;
import service.UnauthorizedException;

public class AuthHandler {
    private final AuthService service;

    public AuthHandler(AuthService service) {
        this.service = service;
    }

    public void login(Context ctx) throws NotFoundException, DataAccessException, UnauthorizedException {
        var gson = new Gson();
        LoginRequest req = gson.fromJson(ctx.body(), LoginRequest.class);
        LoginResult res = service.login(req);
        ctx.json(gson.toJson(res));
    }

    public void logout(Context ctx) throws NotFoundException, DataAccessException {
        var authToken = ctx.header("Authorization");
        service.logout(authToken);
        ctx.status(200);
    }
}
