package handlers;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
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
            } catch (JsonSyntaxException e) {
                ctx.status(400).json("Error: bad request");
            } catch (DataAccessException e) {
                var gson = new Gson();
                System.err.printf(
                        "Database Error: %s%n", e.getMessage()
                );
                ctx.status(500).json(gson.toJson(new ErrorResult("Internal Server Error")));
            } catch (Exception e) {
                var gson = new Gson();
                ctx.status(500).json(gson.toJson(new ErrorResult(e.getMessage())));
            }
        };
    }
}
