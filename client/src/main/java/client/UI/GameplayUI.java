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
        this.game = game;
    }

    @Override
    public String pageIndicator() {
        String modeString = switch (this.mode) {
            case OBSERVER -> "Observing";
            case PLAYER -> "Playing";
        };

        return String.format("%s: %s", modeString, game.gameName());
    }

    @Override
    public UI handleInput(String[] input) {
        return this;
    }
}
