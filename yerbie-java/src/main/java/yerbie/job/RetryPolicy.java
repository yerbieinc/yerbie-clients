package yerbie.job;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.WRAPPER_OBJECT,
    property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = FixedRetryPolicy.class, name = "fixed"),
  @JsonSubTypes.Type(value = ExponentialRetryPolicy.class, name = "exponential")
})
public interface RetryPolicy {
  boolean shouldRetry(int currentRuns);

  long getNextDelaySeconds(int currentRuns);

  long getTotalRetries();
}
