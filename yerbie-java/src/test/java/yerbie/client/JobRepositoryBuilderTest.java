package yerbie.client;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import yerbie.StubData;

@RunWith(MockitoJUnitRunner.class)
public class JobRepositoryBuilderTest {

  @Test
  public void testBuildsCorrectly() throws Exception {
    JobRepositoryBuilder jobRepositoryBuilder = new JobRepositoryBuilder();
    jobRepositoryBuilder.withJobData(StubData.TestJobData.class, StubData.TestJobWithData::new);
    jobRepositoryBuilder.withJobData(StubData.TestJobDataTwo.class, StubData.TestJobWithData::new);

    jobRepositoryBuilder.build();
    assertNotNull(jobRepositoryBuilder.build().findJobForJobClass(StubData.TestJobData.class));
  }
}
