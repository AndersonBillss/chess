package service;

public class AlreadyTakenException extends ServiceException {
    public AlreadyTakenException(String message) {
        super(message);
    }

    public AlreadyTakenException(String message, Throwable ex) {
        super(message, ex);
    }

    @Override
    public int getStatus() {
        return 409;
    }
}
