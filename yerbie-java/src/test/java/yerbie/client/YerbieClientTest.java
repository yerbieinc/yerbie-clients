package yerbie.client;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.Duration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Mono;
import yerbie.StubData;
import yerbie.autogenerated.YerbieAPI;
import yerbie.autogenerated.models.JobRequest;
import yerbie.autogenerated.models.JobToken;
import yerbie.exception.SerializationException;
import yerbie.exception.UnserializableJobDataException;
import yerbie.serde.*;

@RunWith(MockitoJUnitRunner.class)
public class YerbieClientTest {
  @Mock DataTransformer mockDataTransformer;
  @Mock YerbieAPI yerbieAPI;
  @Mock JobSpecTransformer mockJobSpecTransformer;
  @Mock JSONJobDataTransformer jsonJobDataTransformer;

  YerbieClient yerbieClient;

  @Before
  public void setUp() throws Exception {
    yerbieClient = new YerbieClient(yerbieAPI, mockDataTransformer, mockJobSpecTransformer);
    when(mockDataTransformer.getJobDataTransformer(SerializationFormat.JSON))
        .thenReturn(jsonJobDataTransformer);
    when(jsonJobDataTransformer.serializeJobData(StubData.TEST_YERBIE_JOB_DATA))
        .thenReturn(StubData.TEST_JOB_DATA_STRING);
    when(mockJobSpecTransformer.serializeJobSpec(
            new JobSpec(
                StubData.TEST_YERBIE_JOB_DATA.getJobDataClass().getName(),
                StubData.TEST_JOB_DATA_STRING,
                SerializationFormat.JSON,
                StubData.DEFAULT_RETRY_POLICY,
                0)))
        .thenReturn(StubData.TEST_JOB_SPEC_DATA_STRING);

    when(yerbieAPI.scheduleJobAsync(any())).thenReturn(Mono.just(new JobRequest()));
    when(yerbieAPI.deleteJobAsync(any()))
        .thenReturn(Mono.just(new JobToken().setJobToken("jobToken")));
  }

  @Test
  public void testScheduleJobSuccessful() {
    yerbieClient.scheduleJob(Duration.ofSeconds(10), "queue", StubData.TEST_JOB_DATA);
    verify(yerbieAPI)
        .scheduleJobAsync(
            argThat(
                new JobRequestMatcher(
                    new JobRequest()
                        .setDelaySeconds(10)
                        .setQueue("queue")
                        .setJobData(StubData.TEST_JOB_SPEC_DATA_STRING))));
  }

  @Test
  public void testDeleteJob() {
    assertTrue(yerbieClient.deleteJob("jobToken"));
  }

  @Test(expected = UnserializableJobDataException.class)
  public void testSerializationFailure() throws Exception {
    when(jsonJobDataTransformer.serializeJobData(StubData.TEST_YERBIE_JOB_DATA))
        .thenThrow(new SerializationException(new IOException("whoops")));
    yerbieClient.scheduleJob(Duration.ofSeconds(10), "queue", StubData.TEST_JOB_DATA);
  }

  public class JobRequestMatcher implements ArgumentMatcher<JobRequest> {
    private final JobRequest left;

    public JobRequestMatcher(JobRequest left) {
      this.left = left;
    }

    // This does not test job token because it's determined randomly.
    @Override
    public boolean matches(JobRequest right) {
      return right.getDelaySeconds() == left.getDelaySeconds()
          && right.getJobData().equals(left.getJobData())
          && right.getQueue().equals(left.getQueue());
    }
  }
}
