package client.UI;

import client.PromptManager;
import server.ServerFacade;

public class PreloginUI implements UI {

    private ServerFacade facade;
    private PromptManager promptManager;

    public PreloginUI(ServerFacade facade, PromptManager promptManager) {
        this.facade = facade;
        this.promptManager = promptManager;
    }

    @Override
    public UI handleInput(String input) {
        return switch (input.toLowerCase()) {
            case "help" -> help();
            case "quit" -> quit();
            case "login" -> login();
            case "register" -> register();
            default -> this;
        };
    }

    private UI help() {
        return this;
    }

    private UI quit() {
        System.out.println("Goodbye!");
        return null;
    }

    private UI login() {
        return this;
    }

    private UI register() {
        return this;
    }
}
