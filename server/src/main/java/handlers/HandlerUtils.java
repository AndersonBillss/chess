package handlers;

import com.google.gson.Gson;
import dto.ErrorResult;
import io.javalin.http.Handler;
import service.ServiceException;

public class HandlerUtils {
    HandlerUtils() {
    }

    public static Handler handleErr(Handler handlerFn) {
        return ctx -> {
            try {
                handlerFn.handle(ctx);
            } catch (ServiceException e) {
                var gson = new Gson();
                ctx.status(409).json(gson.toJson(new ErrorResult(e.getMessage())));
            }
        };
    }
}
