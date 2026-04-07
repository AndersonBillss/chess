package client;

import client.ui.UI;

import java.util.Scanner;

public class PromptManager {
    private UI currUi;
    private boolean promptLastPrinted = false;
    private int lastPromptLength = 0;

    PromptManager(UI ui) {
        currUi = ui;
    }

    public void printPrompt() {
        promptLastPrinted = true;
        String prompt = String.format("[%s] >>> ", currUi.pageIndicator());
        lastPromptLength = prompt.length();
        System.out.printf(prompt);
    }

    public void println(String line) {
        println(line, false);
    }

    public void println(String line, boolean overridePrompt) {
        print(line, overridePrompt);
        System.out.println();
    }

    public void print(String line) {
        print(line, false);
    }

    public void print(String line, boolean overridePrompt) {
        if (overridePrompt && promptLastPrinted) {
            String lineToPrint = String.format("\r%s", line);
            System.out.print(lineToPrint);
            for (int i = line.length(); i < lastPromptLength; i++) {
                System.out.print(" ");
            }
        } else {
            System.out.print(line);
        }
        promptLastPrinted = false;
    }

    public void clearPrompt() {
        System.out.print("\r");
        if (promptLastPrinted) {
            for (int i = 0; i < lastPromptLength; i++) {
                System.out.print(" ");
            }
        }
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
