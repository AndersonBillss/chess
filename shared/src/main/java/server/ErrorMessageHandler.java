package server;

import websocket.messages.ErrorMessage;

public interface ErrorMessageHandler {
    void handleErr(ErrorMessage message);
}
