package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dto.LoginRequest;
import dto.LoginResult;
import io.javalin.http.Context;
import service.AlreadyTakenException;
import service.AuthService;
import service.NotFoundException;

public class AuthHandler {
    private final AuthService service;

    public AuthHandler() {
        this.service = new AuthService();
    }

    public void login(Context ctx) throws NotFoundException, DataAccessException {
        var gson = new Gson();
        LoginRequest req = gson.fromJson(ctx.body(), LoginRequest.class);
        LoginResult res = service.login(req);
        ctx.json(gson.toJson(res));
    }
}
