package client.UI;

import chess.ChessBoard;
import model.GameData;
import server.ServerFacade;
import ui.EscapeSequences;

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
        printBoard();
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

    private void printBoard() {
        String borderColorEsc = EscapeSequences.SET_BG_COLOR_GREEN;
        String borderTextEsc = EscapeSequences.SET_TEXT_COLOR_BLACK;
        String borderEsc = String.format("%s%s", borderColorEsc, borderTextEsc);
        String resetEsc = String.format("%s%s",
                EscapeSequences.RESET_TEXT_COLOR,
                EscapeSequences.RESET_BG_COLOR);
        String borderRow = String.format(
                "%s%sa%sb%sc%sd%se%sf%sg%sh%s%s",
                borderEsc,
                EscapeSequences.EMPTY,
                EscapeSequences.EMPTY,
                EscapeSequences.EMPTY,
                EscapeSequences.EMPTY,
                EscapeSequences.EMPTY,
                EscapeSequences.EMPTY,
                EscapeSequences.EMPTY,
                EscapeSequences.EMPTY,
                EscapeSequences.EMPTY,
                resetEsc
        );
        System.out.println(borderRow);

        for (int i = 0; i < 8; i++) {
            String borderCell = String.format(
                    "%s %d %s",
                    borderEsc,
                    i,
                    resetEsc
            );
            System.out.print(borderCell);
            for (int j = 1; j < 8; j++) {

            }
            System.out.println(borderCell);
        }

        System.out.println(borderRow);
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
        System.out.print(EscapeSequences.RESET_BG_COLOR);
    }
}
