package yerbie.exception;

public class UnserializableJobDataException extends RuntimeException {

  public UnserializableJobDataException(String message, Throwable cause) {
    super(message, cause);
  }

  public UnserializableJobDataException(String message) {
    super(message);
  }
}
