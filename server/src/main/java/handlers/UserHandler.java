package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dto.RegisterRequest;
import dto.RegisterResult;
import io.javalin.http.Context;
import service.AlreadyTakenException;
import service.UserService;

public class UserHandler {
    private final UserService service;

    public UserHandler(UserService service) {
        this.service = service;
    }

    public void register(Context ctx) throws AlreadyTakenException, DataAccessException {
        var gson = new Gson();
        RegisterRequest req = gson.fromJson(ctx.body(), RegisterRequest.class);
        RegisterResult res = service.register(req);
        ctx.json(gson.toJson(res));
    }
}
