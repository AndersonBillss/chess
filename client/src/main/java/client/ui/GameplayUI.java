package client.ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import client.ClientContext;
import model.GameData;
import ui.EscapeSequences;

import java.util.Objects;

public class GameplayUI implements UI {
    public enum Mode {
        OBSERVER,
        PLAYER,
    }

    private ClientContext ctx;
    private Mode mode;
    private GameData game;
    private String username;
    private ChessGame.TeamColor color;

    public GameplayUI(
            ClientContext ctx, Mode mode, GameData game, String username
    ) {
        this.ctx = ctx;
        this.mode = mode;
        this.game = game;
        this.username = username;
        ctx.setColor(getColor());

        this.ctx.getWebSocketFacade().joinGame(
                this.ctx.getServerFacade().getAuthToken(),
                game.gameID());
    }

    public GameplayUI(
            ClientContext ctx, Mode mode, GameData game, String username, ChessGame.TeamColor color
    ) {
        this(ctx, mode, game, username);
        this.color = color;
    }

    public ChessGame.TeamColor getColor() {
        if (color != null) {
            return color;
        }
        if (mode == Mode.OBSERVER) {
            return ChessGame.TeamColor.WHITE;
        }
        if (Objects.equals(username, game.whiteUsername())) {
            return ChessGame.TeamColor.WHITE;
        }
        if (Objects.equals(username, game.blackUsername())) {
            return ChessGame.TeamColor.BLACK;
        }
        return ChessGame.TeamColor.WHITE;
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
        return switch (input[0].toLowerCase()) {
            case "help" -> help();
            case "leave" -> leave();
            case "move" -> move();
            case "resign" -> resign();
            case "highlight" -> highlight();
            default -> unknownCommand(input);
        };
    }

    private UI help() {
        System.out.println("  Redraw the board: \"redraw\"");
        System.out.println("  Leave the game: \"leave\"");
        System.out.println("  Make a move: \"move\" <START> <END>");
        System.out.println("  Resign from a game: \"resign\"");
        System.out.println("  Highlight legal moves: \"highlight\"");
        return this;
    }

    private UI leave() {
        System.out.println("Not implemented!");
        return this;
    }

    private UI move() {
        System.out.println("Not implemented!");
        return this;
    }

    private UI resign() {
        System.out.println("Not implemented!");
        return this;
    }

    private UI highlight() {
        System.out.println("Not implemented!");
        return this;
    }

    private UI unknownCommand(String[] input) {
        System.out.printf("Unknown command: %s\n", input[0]);
        return this;
    }
}
