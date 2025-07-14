package main.exceptions;

public class IncorrectTaskException extends RuntimeException {
    public IncorrectTaskException(String message) {
        super(message);
    }
}
