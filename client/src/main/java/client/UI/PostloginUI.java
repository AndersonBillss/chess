package client.UI;

import client.PromptManager;
import dto.CreateGameRequest;
import exception.ResponseException;
import model.GameData;
import server.ServerFacade;

import java.io.Console;
import java.lang.reflect.Array;
import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class PostloginUI implements UI {
    private ServerFacade facade;
    private PromptManager promptManager;

    public PostloginUI(ServerFacade facade, PromptManager promptManager) {
        this.facade = facade;
        this.promptManager = promptManager;
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
            default -> this;
        };
    }

    private UI help() {
        System.out.println("HERE IS SOME HELP");
        return this;
    }

    private UI logout() {
        try {
            facade.Logout();
        } catch (ResponseException ignored) {
        }
        promptManager.setLoggedIn(false);
        return new PreloginUI(facade, promptManager);
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
                System.out.printf("  %d - \"%s\": White: \"%s\", \"Black\" %s%n",
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

    private UI join(String[] input) {
        if (input.length != 2) {
            System.out.println("Join requires one argument: <ID>");
            return this;
        }

        ArrayList<GameData> games;
        try {
            games = new ArrayList<>(facade.ListGames().games());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return this;
        }
        int gameIndex;
        try {
            gameIndex = parseInt(input[1].trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number");
            return this;
        }

        if (gameIndex > games.size()) {
            System.out.printf("Game does not exist: %d\n", gameIndex);
            return this;
        }

        int gameId = games.get(gameIndex).gameID();
        return new GameplayUI(facade, promptManager, GameplayUI.Mode.PLAYER, gameId);
    }

    private UI observe(String[] input) {
        return this;
    }
}
