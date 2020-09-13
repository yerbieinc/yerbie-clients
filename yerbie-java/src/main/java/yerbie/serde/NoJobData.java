package yerbie.serde;

public class NoJobData extends JobData<Void> {
  public NoJobData() {
    super(SerializationFormat.NONE, null, Void.class);
  }
}
