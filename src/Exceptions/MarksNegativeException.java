package Exceptions;

public class MarksNegativeException extends RuntimeException {
    public MarksNegativeException(String message) {
        super(message);
    }
}
