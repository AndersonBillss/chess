package client.UI;

import client.PromptManager;
import server.ServerFacade;

public class GameplayUI implements UI {
    public enum Mode {
        OBSERVER,
        PLAYER,
    }

    private ServerFacade facade;
    private Mode mode;
    private int gameId;

    public GameplayUI(
            ServerFacade facade, Mode mode, int gameId
    ) {
        this.facade = facade;
        this.mode = mode;
        this.gameId = gameId;
    }

    @Override
    public String pageIndicator() {
        return "IN GAME";
    }

    @Override
    public UI handleInput(String[] input) {
        return this;
    }
}
