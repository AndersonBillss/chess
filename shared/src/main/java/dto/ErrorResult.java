package dto;

public record ErrorResult(String message) {
    public ErrorResult(Exception e) {
        this(e.getMessage());
    }
}
