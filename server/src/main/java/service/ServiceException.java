package service;

public abstract class ServiceException extends Exception {
    protected ServiceException(String message) {
        super(message);
    }

    protected ServiceException(String message, Throwable ex) {
        super(message, ex);
    }

    public abstract int getStatus();
}
