package service;

public class BadRequestException extends ServiceException {
    public BadRequestException() {
        super("Error: bad request");
    }

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable ex) {
        super(message, ex);
    }

    @Override
    public int getStatus() {
        return 400;
    }
}
