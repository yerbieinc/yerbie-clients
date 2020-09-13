package yerbie.exception;

public class JobNotFoundException extends Exception {

  public JobNotFoundException(Class<?> klass) {
    super("Could not find job for " + klass.getName());
  }
}
