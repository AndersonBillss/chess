package client.websocket;

import websocket.messages.LoadGameMessage;

public interface LoadGameHandler {
    public void load(LoadGameMessage message);
}
