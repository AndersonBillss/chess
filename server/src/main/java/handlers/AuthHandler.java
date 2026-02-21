package handlers;

import com.google.gson.Gson;
import dto.RegisterRequest;
import dto.RegisterResult;
import io.javalin.http.Context;
import service.AuthService;

public class AuthHandler {
    private final AuthService service;

    public AuthHandler() {
        this.service = new AuthService();
    }

    public void register(Context ctx) {
        var gson = new Gson();
        RegisterRequest req = gson.fromJson(ctx.body(), RegisterRequest.class);
        RegisterResult res = service.register(req);
        ctx.result(gson.toJson(res));
    }
}
