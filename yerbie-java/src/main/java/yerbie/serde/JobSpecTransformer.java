package yerbie.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import yerbie.exception.SerializationException;

public class JobSpecTransformer {

  private final ObjectMapper objectMapper;

  public JobSpecTransformer(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public String serializeJobSpec(JobSpec jobSpec) throws SerializationException {
    try {
      return objectMapper.writeValueAsString(jobSpec);
    } catch (IOException ex) {
      throw new SerializationException(ex);
    }
  }

  public JobSpec deserializeJobSpec(String serializedJobSpec) throws SerializationException {
    try {
      return objectMapper.readValue(serializedJobSpec, JobSpec.class);
    } catch (IOException ex) {
      throw new SerializationException(ex);
    }
  }
}
