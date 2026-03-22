package client.UI;

import client.PromptManager;
import dto.CreateGameRequest;
import exception.ResponseException;
import server.ServerFacade;

import java.io.Console;

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
            case "play" -> play(input);
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
            System.out.println("Create requires 1 additional argument:" +
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
            var games = facade.ListGames().games();
            System.out.println("Games:");
            for (var game : games) {
                System.out.printf("  \"%s\": White: \"%s\", \"Black\" %s%n",
                        game.gameName(),
                        game.whiteUsername() == null ? "None" : game.whiteUsername(),
                        game.blackUsername() == null ? "None" : game.blackUsername());
            }
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
        }
        return this;
    }

    private UI play(String[] input) {
        return this;
    }

    private UI observe(String[] input) {
        return this;
    }
}
