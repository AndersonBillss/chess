package handlers;

import com.google.gson.Gson;
import dto.ErrorResult;
import dto.RegisterRequest;
import dto.RegisterResult;
import io.javalin.http.Context;
import service.AlreadyTakenException;
import service.UserService;

public class UserHandler {
    private final UserService service;

    public UserHandler() {
        this.service = new UserService();
    }

    public void register(Context ctx) {
        var gson = new Gson();
        RegisterRequest req = gson.fromJson(ctx.body(), RegisterRequest.class);
        RegisterResult res;
        try {
            res = service.register(req);
        } catch (AlreadyTakenException e) {
            ctx.status(409).json(gson.toJson(new ErrorResult(e)));
            return;
        }
        ctx.json(gson.toJson(res));
    }
}
