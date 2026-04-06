package handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccessException;
import dto.ErrorResult;
import io.javalin.http.Handler;
import io.javalin.websocket.WsMessageHandler;
import service.ServiceException;
import websocket.messages.ErrorMessage;

public class HandlerUtils {
    HandlerUtils() {
    }

    public static WsMessageHandler handleErrWebsocket(WsMessageHandler handlerFn) {
        var gson = new Gson();
        return ctx -> {
            try {
                handlerFn.handleMessage(ctx);
            } catch (DataAccessException e) {
                ErrorMessage errorMessage = new ErrorMessage(
                        String.format("Database Error: %s%n", e.getMessage())
                );
                ctx.session.getRemote().sendString(gson.toJson(errorMessage));
            } catch (Exception e) {
                ErrorMessage errorMessage = new ErrorMessage(
                        String.format("Error: %s%n", e.getMessage())
                );
                ctx.session.getRemote().sendString(gson.toJson(errorMessage));
            }
        };
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
