package main.exceptions;

public class HandlerRequestException extends RuntimeException {
    public HandlerRequestException(String message) {
        super(message);
    }
}
