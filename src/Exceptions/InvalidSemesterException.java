package Exceptions;

public class InvalidSemesterException extends RuntimeException {
  public InvalidSemesterException(String message) {
    super(message);
  }
}
