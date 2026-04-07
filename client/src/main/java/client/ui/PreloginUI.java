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
        ctx.getPromptManager().println("  Register new user: \"register\" <USERNAME> <PASSWORD> <EMAIL>");
        ctx.getPromptManager().println("  Login as existing user: \"login\" <USERNAME> <PASSWORD>");
        ctx.getPromptManager().println("  Exit the program: \"quit\"");
        ctx.getPromptManager().println("  Print this message: \"help\"");
        return this;
    }

    private UI quit() {
        ctx.getPromptManager().println("Goodbye!");
        return null;
    }

    private UI login(String[] input) {
        if (input.length != 3) {
            ctx.getPromptManager().println("Login requires 2 arguments:" +
                    " <USERNAME>, <PASSWORD>");
            return this;
        }
        LoginRequest req = new LoginRequest(input[1], input[2]);
        String username;
        try {
            username = ctx.getServerFacade().login(req).username();
        } catch (ResponseException e) {
            ctx.getPromptManager().println(e.getMessage());
            return this;
        }

        ctx.getPromptManager().println("Successfully logged in.");
        return new PostloginUI(ctx, username);
    }

    private UI register(String[] input) {
        if (input.length != 4) {
            ctx.getPromptManager().println("Register requires 3 arguments:" +
                    " <USERNAME>, <PASSWORD>, <EMAIL>");
            return this;
        }
        RegisterRequest req = new RegisterRequest(input[1], input[2], input[3]);
        String username;
        try {
            username = ctx.getServerFacade().register(req).username();
        } catch (ResponseException e) {
            ctx.getPromptManager().println(e.getMessage());
            return this;
        }

        ctx.getPromptManager().println("Successfully registered.");
        return new PostloginUI(ctx, username);
    }

    private UI unknownCommand(String[] input) {
        ctx.getPromptManager().println("Unknown command: " + input[0]);
        return this;
    }
}
