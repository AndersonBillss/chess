package client.UI;

import dto.CreateGameRequest;
import dto.JoinGameRequest;
import exception.ResponseException;
import model.GameData;
import server.ServerFacade;

import java.util.ArrayList;
import java.util.Objects;

import static java.lang.Integer.parseInt;

public class PostloginUI implements UI {
    private final ServerFacade facade;
    private final String username;

    public PostloginUI(ServerFacade facade, String username) {
        this.facade = facade;
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
        System.out.println("  List current games: \"list\"");
        System.out.println("  Create a new game: \"create\" <NAME>");
        System.out.println("  Join a game: \"join\" <ID> [WHITE|BLACK]");
        System.out.println("  Observe a game: \"observe\" <ID> - a game");
        System.out.println("  Logout: \"logout\"");
        System.out.println("  Print this message: \"help\"\n");
        return this;
    }

    private UI logout() {
        try {
            facade.Logout();
        } catch (ResponseException ignored) {
        }
        return new PreloginUI(facade);
    }

    private UI create(String[] input) {
        if (input.length != 2) {
            System.out.println("Create requires 1 argument:" +
                    " <NAME>");
            return this;
        }
        var req = new CreateGameRequest(input[1]);
        try {
            facade.CreateGame(req);
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
            return this;
        }
        return this;
    }

    private UI list() {
        try {
            var games = new ArrayList<>(facade.ListGames().games());
            System.out.println("Games:");
            for (int i = 0; i < games.size(); i++) {
                var game = games.get(i);
                System.out.printf("  %d. game name: %s    White: %s    Black: %s%n",
                        i + 1,
                        game.gameName(),
                        game.whiteUsername() == null ? "None" : game.whiteUsername(),
                        game.blackUsername() == null ? "None" : game.blackUsername());
            }
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
        }
        return this;
    }

    private GameData getGameFromInput(String gameIndexStr) {
        ArrayList<GameData> games;
        try {
            games = new ArrayList<>(facade.ListGames().games());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
        int gameIndex;
        try {
            gameIndex = parseInt(gameIndexStr.trim()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid number");
            return null;
        }

        if (gameIndex > games.size() || gameIndex < 0) {
            System.out.printf("Game does not exist: %d\n", gameIndex + 1);
            return null;
        }

        return games.get(gameIndex);
    }

    private UI join(String[] input) {
        if (input.length != 3) {
            System.out.println("Join requires two arguments: <ID>, [WHITE|BLACK]");
            return this;
        }
        GameData gameData = getGameFromInput(input[1]);
        if (gameData == null) {
            return this;
        }

        String color = input[2].trim().toUpperCase();
        if (!Objects.equals(color, "WHITE") && !Objects.equals(color, "BLACK")) {
            System.out.println("Color must be black or white");
            return this;
        }

        try {
            JoinGameRequest req = new JoinGameRequest(color, gameData.gameID());
            facade.JoinGame(req);
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
            return this;
        }

        System.out.println("Successfully joined game.");
        return new GameplayUI(
                facade,
                GameplayUI.Mode.PLAYER,
                getGameFromInput(input[1]),
                username);
    }

    private UI observe(String[] input) {
        if (input.length != 2) {
            System.out.println("Join requires one argument: <ID>");
            return this;
        }

        GameData gameData = getGameFromInput(input[1]);
        if (gameData == null) {
            return this;
        }

        return new GameplayUI(facade, GameplayUI.Mode.OBSERVER, gameData, username);
    }

    private UI unknownCommand(String[] input) {
        System.out.printf("Unknown command: %s\n", input[0]);
        return this;
    }
}
