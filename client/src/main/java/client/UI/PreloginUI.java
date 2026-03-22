package client.UI;

import client.PromptManager;
import server.ServerFacade;

public class PreloginUI implements UI {

    private ServerFacade facade;

    public PreloginUI(ServerFacade facade, PromptManager manager) {
        this.facade = facade;
    }

    @Override
    public UI takeInput(String input) {
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
        System.exit(0);
        return null;
    }

    private UI login() {
        return this;
    }

    private UI register() {
        return this;
    }
}
