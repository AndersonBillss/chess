package client.UI;

import client.PromptManager;
import server.ServerFacade;

public class GameplayUI implements UI {
    public enum Mode {
        OBSERVER,
        PLAYER,
    }

    private ServerFacade facade;
    private PromptManager promptManager;
    private Mode mode;
    private int gameId;

    public GameplayUI(
            ServerFacade facade, PromptManager promptManager, Mode mode, int gameId
    ) {
        this.facade = facade;
        this.promptManager = promptManager;
        this.mode = mode;
        this.gameId = gameId;
    }

    @Override
    public UI handleInput(String[] input) {
        return this;
    }
}
