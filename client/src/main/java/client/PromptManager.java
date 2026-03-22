package client;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PromptManager {
    private boolean loggedIn = false;

    public String[] takeInput() {
        String loggedInString = loggedIn ? "IN" : "OUT";
        System.out.printf("[LOGGED %s] >>> ", loggedInString);
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        return input.split(" ");
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
}
