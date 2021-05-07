package yerbie.client;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.util.concurrent.MoreExecutors;
import java.util.concurrent.ExecutorService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Mono;
import yerbie.StubData;
import yerbie.autogenerated.YerbieAPI;
import yerbie.autogenerated.models.JobRequest;
import yerbie.autogenerated.models.Paths1Jciia4JobsFinishedPostResponses200ContentApplicationJsonSchema;
import yerbie.exception.SerializationException;
import yerbie.job.Job;
import yerbie.serde.*;

@RunWith(MockitoJUnitRunner.class)
public class YerbieConsumerTest {
  ExecutorService executorService = MoreExecutors.newDirectExecutorService();
  @Mock YerbieAPI mockYerbieAPI;
  @Mock JobSpecTransformer mockJobSpecTransformer;
  @Mock DataTransformer mockDataTransformer;
  @Mock JobRepository mockJobRepository;
  @Mock JSONJobDataTransformer mockJsonJobDataTransformer;
  @Mock RetryHandler mockRetryHandler;
  YerbieConsumer yerbieConsumer;

  @Before
  public void setUp() {
    yerbieConsumer =
        new YerbieConsumer(
            executorService,
            mockYerbieAPI,
            "queue",
            mockJobSpecTransformer,
            mockDataTransformer,
            mockJobRepository,
            mockRetryHandler);
    when(mockDataTransformer.getJobDataTransformer(SerializationFormat.JSON))
        .thenReturn(mockJsonJobDataTransformer);
    when(mockYerbieAPI.finishedJobAsync(any()))
        .thenReturn(
            Mono.just(new Paths1Jciia4JobsFinishedPostResponses200ContentApplicationJsonSchema()));
  }

  @Test
  public void testFetchReturnsNull() {
    when(mockYerbieAPI.reserveJobAsync("queue")).thenReturn(Mono.just(StubData.EMPTY_JOB_REQUEST));

    assertFalse(yerbieConsumer.fetchAndSubmitOneJob());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testFetchValidJobWithData() throws Exception {
    StubData.TestJobWithData jobToRun = new StubData.TestJobWithData();
    when(mockYerbieAPI.reserveJobAsync("queue")).thenReturn(Mono.just(StubData.VALID_JOB_REQUEST));
    when(mockJobSpecTransformer.deserializeJobSpec(StubData.TEST_JOB_SPEC_DATA_STRING))
        .thenReturn(StubData.TEST_JOB_SPEC);
    when(mockJsonJobDataTransformer.deserializeJobData(
            StubData.TEST_JOB_SPEC.getSerializedJobData(),
            Class.forName(StubData.TEST_JOB_SPEC.getJobClass())))
        .thenReturn((JobData) StubData.TEST_YERBIE_JOB_DATA);
    when(mockJobRepository.findJobForJobData(StubData.TEST_YERBIE_JOB_DATA.getJobData()))
        .thenReturn((Job) jobToRun);

    assertTrue(yerbieConsumer.fetchAndSubmitOneJob());

    verify(mockYerbieAPI).finishedJobAsync("jobToken");
    assertEquals("name", jobToRun.getName());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testFetchValidJobWithException() throws Exception {
    StubData.TestJobThrowsException testJobThrowsException = new StubData.TestJobThrowsException();
    when(mockYerbieAPI.reserveJobAsync("queue")).thenReturn(Mono.just(StubData.VALID_JOB_REQUEST));
    when(mockJobSpecTransformer.deserializeJobSpec(StubData.TEST_JOB_SPEC_DATA_STRING))
        .thenReturn(StubData.TEST_JOB_SPEC);
    when(mockJsonJobDataTransformer.deserializeJobData(
            StubData.TEST_JOB_SPEC.getSerializedJobData(),
            Class.forName(StubData.TEST_JOB_SPEC.getJobClass())))
        .thenReturn((JobData) StubData.TEST_YERBIE_JOB_DATA);
    when(mockJobRepository.findJobForJobData(StubData.TEST_YERBIE_JOB_DATA.getJobData()))
        .thenReturn((Job) testJobThrowsException);

    assertTrue(yerbieConsumer.fetchAndSubmitOneJob());

    verify(mockRetryHandler)
        .handleRetry(
            eq(StubData.TEST_JOB_SPEC), any(JobRequest.class), eq(0), any(RuntimeException.class));
  }

  @Test
  public void testSerializationException() throws Exception {
    when(mockYerbieAPI.reserveJobAsync("queue")).thenReturn(Mono.just(StubData.VALID_JOB_REQUEST));
    when(mockJobSpecTransformer.deserializeJobSpec(StubData.TEST_JOB_SPEC_DATA_STRING))
        .thenThrow(new SerializationException(new RuntimeException()));

    assertTrue(yerbieConsumer.fetchAndSubmitOneJob());
    verify(mockYerbieAPI).finishedJobAsync("jobToken");
  }

  @Test
  public void testClassNotFoundException() throws Exception {
    JobSpec badJobSpec =
        new JobSpec(
            "badClass", "jobData", SerializationFormat.JSON, StubData.DEFAULT_RETRY_POLICY, 0);
    when(mockYerbieAPI.reserveJobAsync("queue")).thenReturn(Mono.just(StubData.VALID_JOB_REQUEST));
    when(mockJobSpecTransformer.deserializeJobSpec(StubData.TEST_JOB_SPEC_DATA_STRING))
        .thenReturn(badJobSpec);

    assertTrue(yerbieConsumer.fetchAndSubmitOneJob());
    verify(mockYerbieAPI).finishedJobAsync("jobToken");
  }
}
