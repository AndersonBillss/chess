package service;

import java.util.UUID;

public class ServiceUtils {
    ServiceUtils() {
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
