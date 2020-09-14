package yerbie.client;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import yerbie.StubData;
import yerbie.exception.JobNotFoundException;

public class JobRepositoryImplTest {

  JobRepository jobRepository;

  @Before
  public void setup() {
    JobRepositoryBuilder jobRepositoryBuilder = new JobRepositoryBuilder();
    jobRepositoryBuilder.withJobData(StubData.TestJobData.class, StubData.TestJobWithData::new);

    jobRepository = jobRepositoryBuilder.build();
  }

  @Test
  public void testFindsJob() throws Exception {
    assertEquals(
        StubData.TestJobWithData.class,
        jobRepository.findJobForJobData(StubData.TEST_JOB_DATA).getClass());
  }

  @Test(expected = JobNotFoundException.class)
  public void testExceptionInvalidClass() throws Exception {
    jobRepository.findJobForJobData("hello");
  }
}
