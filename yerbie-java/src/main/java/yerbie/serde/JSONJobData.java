package yerbie.serde;

public class JSONJobData<D> extends JobData<D> {

  public JSONJobData(D jobData) {
    super(SerializationFormat.JSON, jobData);
  }
}
