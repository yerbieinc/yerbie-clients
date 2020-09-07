package yerbie.exception;

public class SerializationException extends Exception {

  public SerializationException(Throwable cause) {
    super("Error in the serialization layer occurred.", cause);
  }
}
