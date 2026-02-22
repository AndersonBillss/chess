package handlers;

import com.google.gson.Gson;
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

    public void register(Context ctx) throws AlreadyTakenException {
        var gson = new Gson();
        RegisterRequest req = gson.fromJson(ctx.body(), RegisterRequest.class);
        RegisterResult res;
        res = service.register(req);
        ctx.json(gson.toJson(res));
    }
}
