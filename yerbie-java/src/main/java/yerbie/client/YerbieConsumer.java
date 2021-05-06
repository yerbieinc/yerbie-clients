package yerbie.client;

import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yerbie.autogenerated.YerbieAPI;
import yerbie.autogenerated.models.JobRequest;
import yerbie.exception.JobNotFoundException;
import yerbie.exception.SerializationException;
import yerbie.job.Job;
import yerbie.serde.*;

public class YerbieConsumer {
  private static final Logger LOGGER = LoggerFactory.getLogger(YerbieConsumer.class);

  private final ExecutorService jobExecutorService;
  private final ScheduledExecutorService pollingExecutorService;
  private final YerbieAPI yerbieAPI;
  private final String queue;
  private final JobSpecTransformer jobSpecTransformer;
  private final DataTransformer dataTransformer;
  private final JobRepository jobRepository;
  private final RetryHandler retryHandler;
  private boolean processing = false;

  public YerbieConsumer(
      ExecutorService jobExecutorService,
      YerbieAPI yerbieAPI,
      String queue,
      JobSpecTransformer jobSpecTransformer,
      DataTransformer dataTransformer,
      JobRepository jobRepository,
      RetryHandler retryHandler) {
    this.jobExecutorService = jobExecutorService;
    this.yerbieAPI = yerbieAPI;
    this.queue = queue;
    this.jobSpecTransformer = jobSpecTransformer;
    this.dataTransformer = dataTransformer;
    this.jobRepository = jobRepository;
    this.retryHandler = retryHandler;
    this.pollingExecutorService = Executors.newSingleThreadScheduledExecutor();
  }

  public void start() {
    processing = true;

    pollingExecutorService.scheduleWithFixedDelay(
        () -> {
          try {
            executeJobsLoop();
          } catch (Throwable ex) {
            LOGGER.error("Error executing jobs.", ex);
          }
        },
        0,
        1,
        TimeUnit.SECONDS);
  }

  public void shutdown() {
    processing = false;

    jobExecutorService.shutdown();
    pollingExecutorService.shutdown();

    try {
      if (!jobExecutorService.awaitTermination(5, TimeUnit.SECONDS)) {
        jobExecutorService.shutdownNow();
      }
    } catch (InterruptedException ex) {
      jobExecutorService.shutdownNow();
    }

    try {
      if (!pollingExecutorService.awaitTermination(5, TimeUnit.SECONDS)) {
        pollingExecutorService.shutdownNow();
      }
    } catch (InterruptedException ex) {
      pollingExecutorService.shutdownNow();
    }
  }

  private void runJob(Job<Object> job, JobData<?> jobData, JobRequest jobRequest) {

    job.run(jobData.getJobData());

    try {
      yerbieAPI.finishedJobAsync(jobRequest.getJobToken()).block();
    } catch (Exception ex) {
      LOGGER.error("Error marking job as finished after it ran.", ex);
    }
  }

  @VisibleForTesting
  protected boolean fetchAndSubmitOneJob() {
    JobRequest jobRequest = yerbieAPI.reserveJobAsync(queue).block();

    if (jobRequest == null || jobRequest.getJobToken() == null) {
      LOGGER.debug("Received empty jobRequest from Yerbie.");
      return false;
    }

    try {
      JobSpec jobSpec = jobSpecTransformer.deserializeJobSpec(jobRequest.getJobData());
      JobDataTransformer transformer =
          dataTransformer.getJobDataTransformer(jobSpec.getSerializationFormat());
      JobData<?> jobData =
          transformer.deserializeJobData(
              jobSpec.getSerializedJobData(), Class.forName(jobSpec.getJobClass()));

      Job<Object> job = jobRepository.findJobForJobData(jobData.getJobData());

      LOGGER.info(
          "Executing job:{} {} with jobData {}.",
          jobRequest.getJobToken(),
          job.getClass().getName(),
          jobData.getJobData());

      jobExecutorService.submit(
          () -> {
            try {
              runJob(job, jobData, jobRequest);
            } catch (Exception ex) {
              retryHandler.handleRetry(jobSpec, jobRequest, jobSpec.getCurrentRuns() + 1, ex);
            }
          });
    } catch (ClassNotFoundException ex) {
      LOGGER.error("Could not find class for job data {}.", jobRequest.getJobData(), ex);
      yerbieAPI.finishedJobAsync(jobRequest.getJobToken()).block();
    } catch (SerializationException ex) {
      LOGGER.error(
          "Error deserializing job data {}. It will be marked as finished.",
          jobRequest.getJobData(),
          ex);
      yerbieAPI.finishedJobAsync(jobRequest.getJobToken()).block();
    } catch (JobNotFoundException ex) {
      LOGGER.error(
          "Could not find job for given job data. This job will be marked as finished without running.",
          ex);
      yerbieAPI.finishedJobAsync(jobRequest.getJobToken()).block();
    }

    return true;
  }

  private void executeJobsLoop() {
    LOGGER.info("Polling for jobs to execute.");

    while (processing) {
      if (!fetchAndSubmitOneJob()) return;
    }
  }
}
