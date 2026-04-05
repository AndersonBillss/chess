package client.ui;

import client.ClientContext;
import dto.LoginRequest;
import dto.RegisterRequest;
import exception.ResponseException;

public class PreloginUI implements UI {
    ClientContext ctx;

    public PreloginUI(ClientContext ctx) {
        this.ctx = ctx;
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
            default -> unknownCommand(input);
        };
    }

    private UI help() {
        System.out.println("  Register new user: \"register\" <USERNAME> <PASSWORD> <EMAIL>");
        System.out.println("  Login as existing user: \"login\" <USERNAME> <PASSWORD>");
        System.out.println("  Exit the program: \"quit\"");
        System.out.println("  Print this message: \"help\"\n");
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
        String username;
        try {
            username = ctx.getServerFacade().login(req).username();
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
            return this;
        }

        System.out.println("Successfully logged in.");
        return new PostloginUI(ctx, username);
    }

    private UI register(String[] input) {
        if (input.length != 4) {
            System.out.println("Register requires 3 arguments:" +
                    " <USERNAME>, <PASSWORD>, <EMAIL>");
            return this;
        }
        RegisterRequest req = new RegisterRequest(input[1], input[2], input[3]);
        String username;
        try {
            username = ctx.getServerFacade().register(req).username();
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
            return this;
        }

        System.out.println("Successfully registered.");
        return new PostloginUI(ctx, username);
    }

    private UI unknownCommand(String[] input) {
        System.out.printf("Unknown command: %s\n", input[0]);
        return this;
    }
}
