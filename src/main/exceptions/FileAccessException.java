package main.exceptions;

public class FileAccessException extends RuntimeException {
  public FileAccessException(String message) {
      super(message);
  }
}
