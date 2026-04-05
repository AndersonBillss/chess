package client;

import server.ServerFacade;
import server.WebSocketFacade;

public class UIContext {
    private final ServerFacade serverFacade;
    private final WebSocketFacade webSocketFacade;

    UIContext(ServerFacade serverFacade, WebSocketFacade webSocketFacade) {
        this.serverFacade = serverFacade;
        this.webSocketFacade = webSocketFacade;
    }

    public ServerFacade getServerFacade() {
        return serverFacade;
    }

    public WebSocketFacade getWebSocketFacade() {
        return webSocketFacade;
    }
}
