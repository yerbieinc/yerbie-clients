package yerbie.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import yerbie.serde.DataTransformer;
import yerbie.serde.JobSpecTransformer;

public class ClientProvider {
  public static YerbieClient initializeYerbieClient(String host, int port) {
    ObjectMapper objectMapper = new ObjectMapper();
    DataTransformer dataTransformer = new DataTransformer(objectMapper);
    JobSpecTransformer jobSpecTransformer = new JobSpecTransformer(objectMapper);

    return new YerbieClient(host, port, dataTransformer, jobSpecTransformer);
  }
}
