package yerbie.exception;

public class JobSpecSerializationException extends RuntimeException {

  public JobSpecSerializationException(Throwable cause) {
    super("Error in the job spec serialization layer occurred.", cause);
  }
}
