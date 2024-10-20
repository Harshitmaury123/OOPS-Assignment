package Exceptions;

//creates a Exception to tackle invalid Email
public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException(String message) {
        super(message);
    }
}
