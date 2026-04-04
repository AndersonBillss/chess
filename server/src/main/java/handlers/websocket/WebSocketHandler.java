package handlers.websocket;

import com.google.gson.Gson;
import dataaccess.GameDAO;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.util.HashSet;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final SessionManager sessions;
    private final GameDAO gameDao;

    public WebSocketHandler(GameDAO gameDao) {
        this.sessions = new SessionManager();
        this.gameDao = gameDao;
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) throws Exception {

    }

    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) throws Exception {
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) throws Exception {
        var gson = new Gson();
        UserGameCommand command = gson.fromJson(ctx.message(), UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command, ctx);
            case MAKE_MOVE -> makeMove(
                    gson.fromJson(ctx.message(), MakeMoveCommand.class),
                    ctx.session
            );
            case LEAVE -> leave(command, ctx.session);
            case RESIGN -> resign(command, ctx.session);
        }
    }

    private void connect(UserGameCommand command, WsMessageContext ctx) {
        sessions.addSession(command.getGameID(), ctx.session);
        ServerMessage serverMessage = new ServerMessage(
                ServerMessage.ServerMessageType.NOTIFICATION
        );
    }

    private void makeMove(MakeMoveCommand command, Session session) {

    }

    private void leave(UserGameCommand command, Session session) {

    }

    private void resign(UserGameCommand command, Session session) {

    }
}
