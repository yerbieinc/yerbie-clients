package yerbie.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yerbie.autogenerated.YerbieAPI;
import yerbie.autogenerated.models.JobRequest;
import yerbie.exception.SerializationException;
import yerbie.job.RetryPolicy;
import yerbie.serde.JobSpec;
import yerbie.serde.JobSpecTransformer;

public class RetryHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(RetryHandler.class);

  private final YerbieAPI yerbieAPI;
  private final JobSpecTransformer jobSpecTransformer;

  public RetryHandler(YerbieAPI yerbieAPI, JobSpecTransformer jobSpecTransformer) {
    this.yerbieAPI = yerbieAPI;
    this.jobSpecTransformer = jobSpecTransformer;
  }

  public void handleRetry(JobSpec jobSpec, JobRequest jobRequest, int currentRuns, Exception ex) {
    RetryPolicy retryPolicy = jobSpec.getRetryPolicy();

    yerbieAPI.finishedJobAsync(jobRequest.getJobToken()).block();

    int newRuns = currentRuns + 1;

    if (!retryPolicy.shouldRetry(newRuns)) {
      LOGGER.info(
          "Encountered an exception when running. {} with token {} has reached the retry limit of {} and will no longer run. Exception msg: {}",
          jobSpec.getJobClass(),
          jobRequest.getJobToken(),
          retryPolicy.getTotalRetries(),
          ex.getMessage());
      return;
    }

    long nextDelaySeconds = retryPolicy.getNextDelaySeconds(newRuns);

    try {
      JobSpec newJobSpec =
          new JobSpec(
              jobSpec.getJobClass(),
              jobSpec.getSerializedJobData(),
              jobSpec.getSerializationFormat(),
              jobSpec.getRetryPolicy(),
              newRuns);
      String serializedJobSpec = jobSpecTransformer.serializeJobSpec(newJobSpec);

      yerbieAPI
          .scheduleJobAsync(
              jobRequest.setJobData(serializedJobSpec).setDelaySeconds(nextDelaySeconds))
          .block();

      LOGGER.info(
          "Job with token {} encountered exception {}. It has been queued for retry. It has been retried {} times.",
          jobRequest.getJobToken(),
          ex.getMessage(),
          currentRuns);
    } catch (SerializationException serializationException) {
      LOGGER.error("Encountered serialization exception in retry handler.", serializationException);
    }
  }
}
