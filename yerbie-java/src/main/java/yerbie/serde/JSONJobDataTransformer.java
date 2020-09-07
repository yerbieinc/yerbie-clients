package yerbie.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import yerbie.exception.SerializationException;

public class JSONJobDataTransformer implements JobDataTransformer {
  private final ObjectMapper objectMapper;

  public JSONJobDataTransformer(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public <D> String serializeJobData(JobData<D> jobData) throws SerializationException {
    try {
      return objectMapper.writeValueAsString(jobData.getJobData());
    } catch (IOException ex) {
      throw new SerializationException(ex);
    }
  }

  @Override
  public <D> JobData<D> deserializeJobData(String rawJobData, Class<D> jobDataClass)
      throws SerializationException {
    try {
      D jobData = objectMapper.readValue(rawJobData, jobDataClass);
      return new JobData<>(SerializationFormat.JSON, jobData);
    } catch (IOException ex) {
      throw new SerializationException(ex);
    }
  }
}
