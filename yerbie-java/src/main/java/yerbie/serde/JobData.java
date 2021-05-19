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

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof JobData)) {
      return false;
    }

    JobData otherJobData = (JobData) other;

    return this.jobData.equals(otherJobData.jobData)
        && this.serializationFormat.equals(otherJobData.serializationFormat)
        && this.jobDataClass.equals(otherJobData.jobDataClass);
  }
}
