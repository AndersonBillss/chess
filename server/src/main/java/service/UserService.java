package service;

import dto.RegisterRequest;
import dto.RegisterResult;

public class UserService {
    public RegisterResult register(RegisterRequest req) {
        System.out.println("REGISTERED!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        return new RegisterResult("test", "This is a test token");
    }
}
