package yerbie.job;

import static org.junit.Assert.*;

import org.junit.Test;

public class FixedRetryPolicyTest {
  FixedRetryPolicy retryPolicy =
      new FixedRetryPolicy().withTotalRetries(4).withDelayIntervalSeconds(60);

  @Test
  public void testRetryPolicyRetryInterval() {
    assertEquals(60, retryPolicy.getDelayIntervalSeconds());
    assertEquals(4, retryPolicy.getTotalRetries());
  }
}
