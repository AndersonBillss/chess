package handlers;

import service.AuthService;

public class AuthHandler {
    private final AuthService service;

    public AuthHandler() {
        this.service = new AuthService();
    }
}
