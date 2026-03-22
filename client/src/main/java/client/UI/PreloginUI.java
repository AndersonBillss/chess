package client.UI;

import client.PromptManager;
import dto.LoginRequest;
import exception.ResponseException;
import server.ServerFacade;

public class PreloginUI implements UI {

    private ServerFacade facade;
    private PromptManager promptManager;

    public PreloginUI(ServerFacade facade, PromptManager promptManager) {
        this.facade = facade;
        this.promptManager = promptManager;
    }

    @Override
    public UI handleInput(String[] input) {
        return switch (input[0].toLowerCase()) {
            case "help" -> help();
            case "quit" -> quit();
            case "login" -> login(input);
            case "register" -> register();
            default -> this;
        };
    }

    private UI help() {
        System.out.println("HERE IS SOME HELP");
        return this;
    }

    private UI quit() {
        System.out.println("Goodbye!");
        return null;
    }

    private UI login(String[] input) {
        if (input.length != 3) {
            System.out.println("Login requires 2 additional arguments:" +
                    " <USERNAME>, <PASSWORD>");
        }
        LoginRequest req = new LoginRequest(input[0], input[1]);
        try {
            facade.Login(req);
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
            return this;
        }

        promptManager.setLoggedIn(true);
        return new PostloginUI(facade, promptManager);
    }

    private UI register() {
        return this;
    }
}
