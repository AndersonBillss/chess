package client;

import client.ui.UI;

import java.util.Scanner;

public class PromptManager {
    private UI currUi;

    PromptManager(UI ui) {
        currUi = ui;
    }

    public void printPrompt() {
        System.out.printf("[%s] >>> ", currUi.pageIndicator());
    }

    public void takeInput() {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        currUi = currUi.handleInput(input.split(" "));
    }

    public boolean done() {
        return currUi == null;
    }
}
