package yerbie.job;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("fixed")
public class FixedRetryPolicy implements RetryPolicy {
  private long delayIntervalSeconds;
  private int totalRetries;

  @JsonCreator
  public FixedRetryPolicy(
      @JsonProperty("delayIntervalSeconds") long delayIntervalSeconds,
      @JsonProperty("totalRuns") int totalRuns) {
    this.delayIntervalSeconds = delayIntervalSeconds;
    this.totalRetries = totalRuns;
  }

  public FixedRetryPolicy() {}

  public FixedRetryPolicy withDelayIntervalSeconds(long delayIntervalSeconds) {
    this.delayIntervalSeconds = delayIntervalSeconds;
    return this;
  }

  public FixedRetryPolicy withTotalRetries(int totalRetries) {
    this.totalRetries = totalRetries;
    return this;
  }

  public long getDelayIntervalSeconds() {
    return delayIntervalSeconds;
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
    return delayIntervalSeconds;
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof FixedRetryPolicy)) return false;

    FixedRetryPolicy otherPolicy = (FixedRetryPolicy) other;

    return otherPolicy.delayIntervalSeconds == this.delayIntervalSeconds
        && otherPolicy.totalRetries == this.totalRetries;
  }
}
