package yerbie.serde;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class JobSpec {
  private final String jobClass;
  private final String serializedJobData;
  private final SerializationFormat serializationFormat;

  @JsonCreator
  public JobSpec(
      @JsonProperty("jobClass") String jobClass,
      @JsonProperty("serializedJobData") String serializedJobData,
      @JsonProperty("serializationFormat") SerializationFormat serializationFormat) {
    this.jobClass = jobClass;
    this.serializedJobData = serializedJobData;
    this.serializationFormat = serializationFormat;
  }

  public String getJobClass() {
    return jobClass;
  }

  public String getSerializedJobData() {
    return serializedJobData;
  }

  public SerializationFormat getSerializationFormat() {
    return serializationFormat;
  }

  @Override
  public boolean equals(Object otherJobSpec) {
    if (!(otherJobSpec instanceof JobSpec)) return false;

    JobSpec other = (JobSpec) otherJobSpec;

    return this.jobClass.equals(other.jobClass)
        && this.serializedJobData.equals(other.serializedJobData)
        && this.serializationFormat == other.serializationFormat;
  }

  @Override
  public int hashCode() {
    return jobClass.hashCode() + serializedJobData.hashCode() + serializationFormat.hashCode();
  }
}
