package service;

public class NotFoundException extends ServiceException {
    public NotFoundException() {
        super("Error: not found");
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable ex) {
        super(message, ex);
    }

    @Override
    public int getStatus() {
        return 404;
    }
}
