package client.websocket;

import websocket.messages.ErrorMessage;

public interface ErrorMessageHandler {
    void handleErr(ErrorMessage message);
}
