package handlers;

import com.google.gson.Gson;
import dto.RegisterRequest;
import dto.RegisterResult;
import io.javalin.http.Context;
import service.UserService;

public class UserHandler {
    private final UserService service;

    public UserHandler() {
        this.service = new UserService();
    }

    public void register(Context ctx) {
        var gson = new Gson();
        RegisterRequest req = gson.fromJson(ctx.body(), RegisterRequest.class);
        RegisterResult res = service.register(req);
        ctx.result(gson.toJson(res));
    }
}
