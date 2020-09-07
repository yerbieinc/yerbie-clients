package yerbie.serde;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import yerbie.StubData;

public class JSONJobDataTransformerTest {

  JSONJobDataTransformer jsonJobDataTransformer;

  @Before
  public void setUp() {
    jsonJobDataTransformer = new JSONJobDataTransformer(new ObjectMapper());
  }

  @Test
  public void testSerialize() throws Exception {
    String serializedJobData =
        jsonJobDataTransformer.serializeJobData(StubData.TEST_YERBIE_JOB_DATA);
    assertEquals(StubData.TEST_JOB_DATA_STRING, serializedJobData);
  }

  @Test
  public void testDeserialize() throws Exception {
    JobData<StubData.TestJobData> jobData =
        jsonJobDataTransformer.deserializeJobData(
            StubData.TEST_JOB_DATA_STRING, StubData.TestJobData.class);

    assertEquals(StubData.TEST_YERBIE_JOB_DATA.getJobData(), jobData.getJobData());
    assertEquals(
        StubData.TEST_YERBIE_JOB_DATA.getSerializationFormat(), jobData.getSerializationFormat());
    assertEquals(StubData.TEST_YERBIE_JOB_DATA.getJobDataClass(), jobData.getJobDataClass());
  }

  @Test
  public void testSerializeDeserialize() throws Exception {
    assertEquals(
        StubData.TEST_JOB_DATA_STRING,
        jsonJobDataTransformer.serializeJobData(
            jsonJobDataTransformer.deserializeJobData(
                jsonJobDataTransformer.serializeJobData(StubData.TEST_YERBIE_JOB_DATA),
                StubData.TestJobData.class)));
  }
}
