package client.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import jakarta.websocket.*;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    Session session;
    ErrorMessageHandler errorMessageHandler;
    LoadGameHandler loadGameHandler;
    NotificationHandler notificationHandler;

    public WebSocketFacade(
            String url,
            ErrorMessageHandler errorMessageHandler,
            LoadGameHandler loadGameHandler,
            NotificationHandler notificationHandler
    ) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.errorMessageHandler = errorMessageHandler;
            this.loadGameHandler = loadGameHandler;
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new jakarta.websocket.MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    var gson = new Gson();
                    ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
                    switch (serverMessage.getServerMessageType()) {
                        case LOAD_GAME -> {
                            LoadGameMessage loadGameMessage = gson.fromJson(
                                    message,
                                    LoadGameMessage.class
                            );
                            loadGameHandler.load(loadGameMessage);
                        }
                        case ERROR -> {
                            ErrorMessage errorMessage = gson.fromJson(
                                    message,
                                    ErrorMessage.class
                            );
                            errorMessageHandler.handleErr(errorMessage);
                        }
                        case NOTIFICATION -> {
                            NotificationMessage notificationMessage = gson.fromJson(
                                    message,
                                    NotificationMessage.class
                            );
                            notificationHandler.notify(notificationMessage);
                        }
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void joinGame(String authToken, int gameId) {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameId);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public void leaveGame(String authToken, int gameId) {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameId);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public void resignGame(String authToken, int gameId) {
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameId);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public void makeMove(String authToken, int gameId, ChessMove move) {
        try {
            var command = new MakeMoveCommand(authToken, gameId, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
