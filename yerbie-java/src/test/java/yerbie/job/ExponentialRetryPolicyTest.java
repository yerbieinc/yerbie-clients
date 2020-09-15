package yerbie.job;

import static org.junit.Assert.*;

import org.junit.Test;

public class ExponentialRetryPolicyTest {

  ExponentialRetryPolicy exponentialRetryPolicy =
      new ExponentialRetryPolicy().withTotalRetries(4).withInitialDelayIntervalSeconds(2);

  @Test
  public void testExponentialDelay() {
    assertEquals(2, exponentialRetryPolicy.getNextDelaySeconds(1));
    assertEquals(4, exponentialRetryPolicy.getNextDelaySeconds(2));
    assertEquals(8, exponentialRetryPolicy.getNextDelaySeconds(3));
    assertEquals(16, exponentialRetryPolicy.getNextDelaySeconds(4));
    assertEquals(32, exponentialRetryPolicy.getNextDelaySeconds(5));
  }
}
