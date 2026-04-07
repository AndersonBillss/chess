package client.ui;

import chess.ChessGame;
import client.ClientContext;
import dto.CreateGameRequest;
import dto.JoinGameRequest;
import exception.ResponseException;
import model.GameData;

import java.util.ArrayList;
import java.util.Objects;

import static java.lang.Integer.parseInt;

public class PostloginUI implements UI {
    private final ClientContext ctx;
    private final String username;

    public PostloginUI(
            ClientContext uiContext,
            String username) {
        this.ctx = uiContext;
        this.username = username;
    }

    @Override
    public String pageIndicator() {
        return "Logged in";
    }

    @Override
    public UI handleInput(String[] input) {
        return switch (input[0].toLowerCase()) {
            case "help" -> help();
            case "logout" -> logout();
            case "create" -> create(input);
            case "list" -> list();
            case "join" -> join(input);
            case "observe" -> observe(input);
            default -> unknownCommand(input);
        };
    }

    private UI help() {
        ctx.getPromptManager().println("  List current games: \"list\"");
        ctx.getPromptManager().println("  Create a new game: \"create\" <NAME>");
        ctx.getPromptManager().println("  Join a game: \"join\" <ID> [WHITE|BLACK]");
        ctx.getPromptManager().println("  Observe a game: \"observe\" <ID> - a game");
        ctx.getPromptManager().println("  Logout: \"logout\"");
        ctx.getPromptManager().println("  Print this message: \"help\"\n");
        return this;
    }

    private UI logout() {
        try {
            ctx.getServerFacade().logout();
        } catch (ResponseException ignored) {
        }
        return new PreloginUI(ctx);
    }

    private UI create(String[] input) {
        if (input.length != 2) {
            ctx.getPromptManager().println("Create requires 1 argument:" +
                    " <NAME>");
            return this;
        }
        var req = new CreateGameRequest(input[1]);
        try {
            ctx.getServerFacade().createGame(req);
        } catch (ResponseException e) {
            ctx.getPromptManager().println(e.getMessage());
            return this;
        }
        return this;
    }

    private UI list() {
        try {
            var games = new ArrayList<>(ctx.getServerFacade().listGames().games());
            ctx.getPromptManager().println("Games:");
            for (int i = 0; i < games.size(); i++) {
                var game = games.get(i);
                ctx.getPromptManager().println(String.format(
                        "  %d. game name: %s    White: %s    Black: %s",
                        i + 1,
                        game.gameName(),
                        game.whiteUsername() == null ? "None" : game.whiteUsername(),
                        game.blackUsername() == null ? "None" : game.blackUsername()));
            }
        } catch (ResponseException e) {
            ctx.getPromptManager().println(e.getMessage());
        }
        return this;
    }

    private GameData getGameFromInput(String gameIndexStr) {
        ArrayList<GameData> games;
        try {
            games = new ArrayList<>(ctx.getServerFacade().listGames().games());
        } catch (Exception e) {
            ctx.getPromptManager().println(e.getMessage());
            return null;
        }
        int gameIndex;
        try {
            gameIndex = parseInt(gameIndexStr.trim()) - 1;
        } catch (NumberFormatException e) {
            ctx.getPromptManager().println("Invalid number");
            return null;
        }

        if (gameIndex > games.size() || gameIndex < 0) {
            ctx.getPromptManager().println("Game does not exist: " + (gameIndex + 1));
            return null;
        }

        return games.get(gameIndex);
    }

    private UI join(String[] input) {
        if (input.length != 3) {
            ctx.getPromptManager().println("Join requires two arguments: <ID>, [WHITE|BLACK]");
            return this;
        }
        GameData gameData = getGameFromInput(input[1]);
        if (gameData == null) {
            return this;
        }

        String color = input[2].trim().toUpperCase();
        if (!Objects.equals(color, "WHITE") && !Objects.equals(color, "BLACK")) {
            ctx.getPromptManager().println("Color must be black or white");
            return this;
        }

        try {
            JoinGameRequest req = new JoinGameRequest(color, gameData.gameID());
            ctx.getServerFacade().joinGame(req);
        } catch (ResponseException e) {
            ctx.getPromptManager().println(e.getMessage());
            return this;
        }

        ChessGame.TeamColor teamColor = Objects.equals(color, "BLACK") ? ChessGame.TeamColor.BLACK
                : ChessGame.TeamColor.WHITE;
        ctx.getPromptManager().println("Successfully joined game.");
        return new GameplayUI(
                ctx,
                GameplayUI.Mode.PLAYER,
                getGameFromInput(input[1]),
                username,
                teamColor);
    }

    private UI observe(String[] input) {
        if (input.length != 2) {
            ctx.getPromptManager().println("Observe requires one argument: <ID>");
            return this;
        }

        GameData gameData = getGameFromInput(input[1]);
        if (gameData == null) {
            return this;
        }

        return new GameplayUI(
                ctx,
                GameplayUI.Mode.OBSERVER,
                gameData, username);
    }

    private UI unknownCommand(String[] input) {
        ctx.getPromptManager().println("Unknown command: " + input[0]);
        return this;
    }
}
