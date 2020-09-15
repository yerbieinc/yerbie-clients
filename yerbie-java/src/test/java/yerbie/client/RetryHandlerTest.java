package yerbie.client;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import yerbie.StubData;
import yerbie.autogenerated.YerbieAPI;
import yerbie.serde.JobSpec;
import yerbie.serde.JobSpecTransformer;

@RunWith(MockitoJUnitRunner.class)
public class RetryHandlerTest {
  @Mock YerbieAPI mockYerbieAPI;
  @Mock JobSpecTransformer mockJobSpecTransformer;
  RetryHandler retryHandler;

  @Before
  public void setUp() {
    retryHandler = new RetryHandler(mockYerbieAPI, mockJobSpecTransformer);
  }

  @Test
  public void testRetriesFinished() {
    retryHandler.handleRetry(
        StubData.TEST_JOB_SPEC, StubData.VALID_JOB_REQUEST, 5, new RuntimeException("ex"));
    verify(mockYerbieAPI).finishedJob(StubData.VALID_JOB_REQUEST.jobToken());
    verify(mockYerbieAPI, never()).scheduleJob(any());
  }

  @Test
  public void testRetriesNotFinished() throws Exception {
    JobSpec newJobSpec =
        new JobSpec(
            StubData.TEST_JOB_SPEC.getJobClass(),
            StubData.TEST_JOB_SPEC.getSerializedJobData(),
            StubData.TEST_JOB_SPEC.getSerializationFormat(),
            StubData.TEST_JOB_SPEC.getRetryPolicy(),
            1);
    when(mockJobSpecTransformer.serializeJobSpec(newJobSpec))
        .thenReturn(StubData.TEST_JOB_SPEC_DATA_STRING);

    retryHandler.handleRetry(
        StubData.TEST_JOB_SPEC, StubData.VALID_JOB_REQUEST, 1, new RuntimeException("ex"));

    verify(mockYerbieAPI).finishedJob(StubData.VALID_JOB_REQUEST.jobToken());
    verify(mockYerbieAPI)
        .scheduleJob(
            StubData.VALID_JOB_REQUEST
                .withJobData(StubData.TEST_JOB_SPEC_DATA_STRING)
                .withDelaySeconds(30));
  }
}
