package service;

public class UnauthorizedException extends ServiceException {
    public UnauthorizedException() {
        super("Error: unauthorized");
    }

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable ex) {
        super(message, ex);
    }

    @Override
    public int getStatus() {
        return 401;
    }
}

