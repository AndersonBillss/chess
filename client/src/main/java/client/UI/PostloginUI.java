package client.UI;

import client.PromptManager;
import server.ServerFacade;

public class PostloginUI implements UI {
    private ServerFacade facade;
    private PromptManager promptManager;

    public PostloginUI(ServerFacade facade, PromptManager promptManager) {
        this.facade = facade;
        this.promptManager = promptManager;
    }

    @Override
    public UI handleInput(String[] input) {
        return this;
    }
}
