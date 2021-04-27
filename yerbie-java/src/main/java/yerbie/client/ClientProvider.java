package yerbie.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import yerbie.autogenerated.YerbieAPI;
import yerbie.autogenerated.implementation.YerbieAPIImplBuilder;
import yerbie.serde.DataTransformer;
import yerbie.serde.JobSpecTransformer;

public class ClientProvider {
  private static final String URL_FORMAT_STRING = "%s:%d";

  private final String host;
  private final int port;

  public ClientProvider(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public YerbieClient initializeClient(ObjectMapper objectMapper) {
    DataTransformer dataTransformer = new DataTransformer(objectMapper);

    // We want to use our own object mapper for internal classes.
    JobSpecTransformer jobSpecTransformer = new JobSpecTransformer(new ObjectMapper());

    return new YerbieClient(host, port, dataTransformer, jobSpecTransformer);
  }

  public YerbieClient initializeYerbieClient() {
    ObjectMapper objectMapper = new ObjectMapper();
    return initializeClient(objectMapper);
  }

  public YerbieConsumer initializeYerbieConsumer(
      String queue,
      JobRepository jobRepository,
      ObjectMapper objectMapper,
      ExecutorService executorService) {

    YerbieAPI yerbieAPI =
        new YerbieAPIImplBuilder().host(String.format(URL_FORMAT_STRING, host, port)).buildClient();
    DataTransformer dataTransformer = new DataTransformer(objectMapper);

    // We want to use our own object mapper for internal classes.
    JobSpecTransformer jobSpecTransformer = new JobSpecTransformer(new ObjectMapper());

    RetryHandler retryHandler = new RetryHandler(yerbieAPI, jobSpecTransformer);

    return new YerbieConsumer(
        executorService,
        yerbieAPI,
        queue,
        jobSpecTransformer,
        dataTransformer,
        jobRepository,
        retryHandler);
  }

  public YerbieConsumer initializeYerbieConsumer(String queue, JobRepository jobRepository) {
    ObjectMapper objectMapper = new ObjectMapper();
    return initializeYerbieConsumer(
        queue, jobRepository, objectMapper, Executors.newFixedThreadPool(10));
  }
}
