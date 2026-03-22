package client.UI;

import client.PromptManager;
import server.ServerFacade;

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
            case "list" -> list(input);
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
        return this;
    }

    private UI create(String[] input) {
        return this;
    }

    private UI list(String[] input) {
        return this;
    }

    private UI play(String[] input) {
        return this;
    }

    private UI observe(String[] input) {
        return this;
    }
}
