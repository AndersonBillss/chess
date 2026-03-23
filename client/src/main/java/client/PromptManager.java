package client;

import client.ui.UI;

import java.util.Scanner;

public class PromptManager {
    private boolean loggedIn = false;

    public UI takeInput(UI ui) {
        System.out.printf("[%s] >>> ", ui.pageIndicator());
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        return ui.handleInput(input.split(" "));
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
}
