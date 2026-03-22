package client.UI;

import dto.LoginRequest;
import dto.RegisterRequest;
import exception.ResponseException;
import server.ServerFacade;

public class PreloginUI implements UI {

    private ServerFacade facade;

    public PreloginUI(ServerFacade facade) {
        this.facade = facade;
    }

    @Override
    public String pageIndicator() {
        return "Logged out";
    }

    @Override
    public UI handleInput(String[] input) {
        return switch (input[0].toLowerCase()) {
            case "help" -> help();
            case "quit" -> quit();
            case "login" -> login(input);
            case "register" -> register(input);
            default -> this;
        };
    }

    private UI help() {
        System.out.println("  register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
        System.out.println("  login <USERNAME> <PASSWORD> - to play chess");
        System.out.println("  quit - playing chess");
        System.out.println("  help - with possible commands\n");
        return this;
    }

    private UI quit() {
        System.out.println("Goodbye!");
        return null;
    }

    private UI login(String[] input) {
        if (input.length != 3) {
            System.out.println("Login requires 2 arguments:" +
                    " <USERNAME>, <PASSWORD>");
            return this;
        }
        LoginRequest req = new LoginRequest(input[1], input[2]);
        try {
            facade.Login(req);
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
            return this;
        }

        return new PostloginUI(facade);
    }

    private UI register(String[] input) {
        if (input.length != 4) {
            System.out.println("Register requires 3 arguments:" +
                    " <USERNAME>, <PASSWORD>, <EMAIL>");
            return this;
        }
        RegisterRequest req = new RegisterRequest(input[1], input[2], input[3]);
        try {
            facade.Register(req);
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
            return this;
        }

        return new PostloginUI(facade);
    }
}
