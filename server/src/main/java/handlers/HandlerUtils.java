package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
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
                ctx.status(e.getStatus()).json(gson.toJson(new ErrorResult(e.getMessage())));
            } catch (DataAccessException e) {
                var gson = new Gson();
                ctx.status(500).json(gson.toJson(new ErrorResult("Error: could not connect to database")));
            }
        };
    }
}
