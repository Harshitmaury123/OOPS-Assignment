package Exceptions;

public class InvalidProfessorIdException extends RuntimeException {
    public InvalidProfessorIdException(String message) {
        super(message);
    }
}
