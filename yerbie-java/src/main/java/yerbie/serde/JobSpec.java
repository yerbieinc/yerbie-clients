package yerbie.serde;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import yerbie.job.RetryPolicy;

public class JobSpec {
  private final String jobClass;
  private final String serializedJobData;
  private final SerializationFormat serializationFormat;
  private final RetryPolicy retryPolicy;
  private final int currentRuns;

  @JsonCreator
  public JobSpec(
      @JsonProperty("jobClass") String jobClass,
      @JsonProperty("serializedJobData") String serializedJobData,
      @JsonProperty("serializationFormat") SerializationFormat serializationFormat,
      @JsonProperty("retryPolicy") RetryPolicy retryPolicy,
      @JsonProperty("currentRuns") int currentRuns) {
    this.jobClass = jobClass;
    this.serializedJobData = serializedJobData;
    this.serializationFormat = serializationFormat;
    this.retryPolicy = retryPolicy;
    this.currentRuns = currentRuns;
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

  public RetryPolicy getRetryPolicy() {
    return retryPolicy;
  }

  public int getCurrentRuns() {
    return currentRuns;
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
