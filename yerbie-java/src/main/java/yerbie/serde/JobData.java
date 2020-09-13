package yerbie.serde;

public class JobData<D> {
  private final SerializationFormat serializationFormat;
  private final Class jobDataClass;
  private final D jobData;

  public JobData(SerializationFormat serializationFormat, D jobData) {
    this.serializationFormat = serializationFormat;
    this.jobData = jobData;
    this.jobDataClass = jobData.getClass();
  }

  public JobData(SerializationFormat serializationFormat, D jobData, Class jobDataClass) {
    this.serializationFormat = serializationFormat;
    this.jobData = jobData;
    this.jobDataClass = jobDataClass;
  }

  public D getJobData() {
    return jobData;
  }

  public Class<D> getJobDataClass() {
    return jobDataClass;
  }

  public SerializationFormat getSerializationFormat() {
    return serializationFormat;
  }
}
