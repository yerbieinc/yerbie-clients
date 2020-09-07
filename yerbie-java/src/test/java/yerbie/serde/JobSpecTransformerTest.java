package yerbie.serde;

import static org.junit.Assert.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import yerbie.StubData;

public class JobSpecTransformerTest {

  JobSpecTransformer jobSpecTransformer;

  @Before
  public void setUp() {
    jobSpecTransformer = new JobSpecTransformer(new ObjectMapper());
  }

  @Test
  public void testSerialize() throws Exception {
    assertEquals(
        StubData.TEST_JOB_SPEC_DATA_STRING,
        jobSpecTransformer.serializeJobSpec(StubData.TEST_JOB_SPEC));
  }

  @Test
  public void testDeserialize() throws Exception {
    JobSpec jobSpec = jobSpecTransformer.deserializeJobSpec(StubData.TEST_JOB_SPEC_DATA_STRING);
    assertEquals(StubData.TEST_JOB_SPEC.getJobClass(), jobSpec.getJobClass());
    assertEquals(StubData.TEST_JOB_SPEC.getSerializedJobData(), jobSpec.getSerializedJobData());
  }
}
