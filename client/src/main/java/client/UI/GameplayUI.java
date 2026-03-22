package client.UI;

import model.GameData;
import server.ServerFacade;

public class GameplayUI implements UI {
    public enum Mode {
        OBSERVER,
        PLAYER,
    }

    private ServerFacade facade;
    private Mode mode;
    private GameData game;

    public GameplayUI(
            ServerFacade facade, Mode mode, GameData game
    ) {
        this.facade = facade;
        this.mode = mode;
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
