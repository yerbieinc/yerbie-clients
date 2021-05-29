package yerbie.client;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Mono;
import yerbie.StubData;
import yerbie.autogenerated.YerbieAPI;
import yerbie.autogenerated.models.JobRequest;
import yerbie.autogenerated.models.JobToken;
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
    when(mockYerbieAPI.finishedJobAsync(StubData.VALID_JOB_REQUEST.getJobToken()))
        .thenReturn(Mono.just(new JobToken()));
    when(mockYerbieAPI.scheduleJobAsync(any())).thenReturn(Mono.just(new JobRequest()));
  }

  @Test
  public void testRetriesFinished() {
    retryHandler.handleRetry(
        StubData.TEST_JOB_SPEC, StubData.VALID_JOB_REQUEST, 5, new RuntimeException("ex"));
    verify(mockYerbieAPI).finishedJobAsync(StubData.VALID_JOB_REQUEST.getJobToken());
    verify(mockYerbieAPI, never()).scheduleJobAsync(any());
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

    verify(mockYerbieAPI).finishedJobAsync(StubData.VALID_JOB_REQUEST.getJobToken());
    verify(mockYerbieAPI)
        .scheduleJobAsync(
            StubData.VALID_JOB_REQUEST
                .setJobData(StubData.TEST_JOB_SPEC_DATA_STRING)
                .setDelaySeconds(30));
  }
}
