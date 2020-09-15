package yerbie.job;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("exponential")
public class ExponentialRetryPolicy implements RetryPolicy {
  private long initialDelaySeconds;
  private int totalRetries;

  @JsonCreator
  public ExponentialRetryPolicy(
      @JsonProperty("initialDelaySeconds") long initialDelaySeconds,
      @JsonProperty("totalRuns") int totalRuns) {
    this.initialDelaySeconds = initialDelaySeconds;
    this.totalRetries = totalRuns;
  }

  public ExponentialRetryPolicy() {}

  public ExponentialRetryPolicy withInitialDelayIntervalSeconds(long delayIntervalSeconds) {
    this.initialDelaySeconds = delayIntervalSeconds;
    return this;
  }

  public ExponentialRetryPolicy withTotalRetries(int totalRetries) {
    this.totalRetries = totalRetries;
    return this;
  }

  public long getInitialDelaySeconds() {
    return initialDelaySeconds;
  }

  @Override
  public long getTotalRetries() {
    return totalRetries;
  }

  // If the current run is 2, then it's been retried a total of 1 time.
  // Therefore, currentRuns - 1 must be less than the total retries.
  @Override
  public boolean shouldRetry(int currentRuns) {
    return currentRuns - 1 < totalRetries;
  }

  @Override
  public long getNextDelaySeconds(int currentRuns) {
    return Double.valueOf(Math.pow(initialDelaySeconds, currentRuns)).longValue();
  }
}
