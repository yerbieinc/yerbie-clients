package yerbie.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.NotImplementedException;

public class DataTransformer {

  private final ObjectMapper objectMapper;

  public DataTransformer(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public JobDataTransformer getJobDataTransformer(SerializationFormat serializationFormat) {
    if (serializationFormat == SerializationFormat.JSON) {
      return new JSONJobDataTransformer(objectMapper);
    }

    throw new NotImplementedException("Only JSON Job Data Transformers are implemented now.");
  }
}
