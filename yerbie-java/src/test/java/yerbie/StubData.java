package yerbie;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import yerbie.serde.JSONJobData;
import yerbie.serde.JobData;
import yerbie.serde.JobSpec;

public class StubData {
  public static TestJobData TEST_JOB_DATA = new TestJobData("name");
  public static JobData<TestJobData> TEST_YERBIE_JOB_DATA = new JSONJobData<>(TEST_JOB_DATA);
  public static String TEST_JOB_DATA_STRING = "{\"name\":\"name\"}";

  public static String TEST_JOB_SPEC_DATA_STRING =
      "{\"jobClass\":\"yerbie.StubData$TestJobData\",\"serializedJobData\":\"jobData\"}";
  public static JobSpec TEST_JOB_SPEC = new JobSpec(TestJobData.class.getName(), "jobData");

  public static class TestJobData {
    String name;

    @JsonCreator
    public TestJobData(@JsonProperty("name") String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    @Override
    public boolean equals(Object other) {
      if (!(other instanceof TestJobData)) return false;

      return this.name.equals(((TestJobData) other).getName());
    }
  }
}
