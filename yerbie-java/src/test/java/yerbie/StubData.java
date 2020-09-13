package yerbie;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import yerbie.autogenerated.models.JobRequest;
import yerbie.job.Job;
import yerbie.serde.*;

public class StubData {
  public static TestJobData TEST_JOB_DATA = new TestJobData("name");
  public static JobData<TestJobData> TEST_YERBIE_JOB_DATA = new JSONJobData<>(TEST_JOB_DATA);
  public static String TEST_JOB_DATA_STRING = "{\"name\":\"name\"}";

  public static String TEST_JOB_SPEC_DATA_STRING =
      "{\"jobClass\":\"yerbie.StubData$TestJobData\",\"serializedJobData\":\"jobData\",\"serializationFormat\":\"JSON\"}";
  public static JobSpec TEST_JOB_SPEC =
      new JobSpec(TestJobData.class.getName(), "jobData", SerializationFormat.JSON);

  public static JobRequest EMPTY_JOB_REQUEST = new JobRequest();

  public static JobRequest VALID_JOB_REQUEST =
      new JobRequest()
          .withJobData(TEST_JOB_SPEC_DATA_STRING)
          .withJobToken("jobToken")
          .withDelaySeconds(10)
          .withQueue("queue");

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

  public static class TestJob implements Job<Void> {
    private int counter;

    @Override
    public void run(Void nothing) {
      counter++;
    }

    public int getCounter() {
      return counter;
    }
  }

  public static class TestJobWithData implements Job<TestJobData> {
    private String name;

    @Override
    public void run(TestJobData jobData) {
      this.name = jobData.getName();
    }

    public String getName() {
      return name;
    }
  }
}
