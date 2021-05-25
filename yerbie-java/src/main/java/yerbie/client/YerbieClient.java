/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package yerbie.client;

import java.util.UUID;
import yerbie.autogenerated.YerbieAPI;
import yerbie.autogenerated.implementation.YerbieAPIImplBuilder;
import yerbie.autogenerated.models.JobRequest;
import yerbie.exception.InvalidJobDataClass;
import yerbie.exception.SerializationException;
import yerbie.exception.UnserializableJobDataException;
import yerbie.job.FixedRetryPolicy;
import yerbie.job.RetryPolicy;
import yerbie.serde.*;

public class YerbieClient {
  private static final String URL_FORMAT_STRING = "%s:%d";
  private static final RetryPolicy DEFAULT_RETRY_POLICY =
      new FixedRetryPolicy().withDelayIntervalSeconds(30).withTotalRetries(4);
  private final YerbieAPI yerbieAPI;
  private final DataTransformer dataTransformer;
  private final JobSpecTransformer jobSpecTransformer;

  public YerbieClient(
      String host,
      long port,
      DataTransformer dataTransformer,
      JobSpecTransformer jobSpecTransformer) {
    this(
        new YerbieAPIImplBuilder().host(String.format(URL_FORMAT_STRING, host, port)).buildClient(),
        dataTransformer,
        jobSpecTransformer);
  }

  public YerbieClient(
      YerbieAPI yerbieAPI, DataTransformer dataTransformer, JobSpecTransformer jobSpecTransformer) {
    this.yerbieAPI = yerbieAPI;
    this.dataTransformer = dataTransformer;
    this.jobSpecTransformer = jobSpecTransformer;
  }

  public <D> String scheduleAsyncJob(String queue, D jobData) {
    return schedule(0, queue, jobData, DEFAULT_RETRY_POLICY, SerializationFormat.JSON);
  }

  public <D> String scheduleJob(long delaySeconds, String queue, D jobData) {
    return schedule(delaySeconds, queue, jobData, DEFAULT_RETRY_POLICY, SerializationFormat.JSON);
  }

  public <D> String scheduleJob(
      long delaySeconds, String queue, D jobData, RetryPolicy retryPolicy) {
    return schedule(delaySeconds, queue, jobData, retryPolicy, SerializationFormat.JSON);
  }

  public String deleteJob(String jobToken) {
    return yerbieAPI.deleteJobAsync(jobToken).block().getJobToken();
  }

  private <D> String schedule(
      long delaySeconds,
      String queue,
      D jobData,
      RetryPolicy retryPolicy,
      SerializationFormat serializationFormat) {
    JobData<D> wrappedJobData = getWrappedJobData(jobData, serializationFormat);

    try {
      JobDataTransformer jobDataTransformer =
          dataTransformer.getJobDataTransformer(wrappedJobData.getSerializationFormat());
      String jobToken = UUID.randomUUID().toString();
      String serializedJobData = jobDataTransformer.serializeJobData(wrappedJobData);

      JobSpec jobSpec =
          new JobSpec(
              wrappedJobData.getJobDataClass().getName(),
              serializedJobData,
              wrappedJobData.getSerializationFormat(),
              retryPolicy,
              0);

      // Ensure that it's possible to deserialize the job data before sending it over to store.
      JobData<D> jobDataDeserialized =
          jobDataTransformer.deserializeJobData(
              jobSpec.getSerializedJobData(), (Class<D>) Class.forName(jobSpec.getJobClass()));

      String serializedJobSpec = jobSpecTransformer.serializeJobSpec(jobSpec);

      JobRequest jobRequest =
          new JobRequest()
              .setJobData(serializedJobSpec)
              .setJobToken(jobToken)
              .setDelaySeconds(delaySeconds)
              .setQueue(queue);

      yerbieAPI.scheduleJobAsync(jobRequest).block();
      return jobToken;
    } catch (ClassNotFoundException ex) {
      throw new InvalidJobDataClass(
          String.format(
              "Cannot load the job data class for job data class %s.",
              wrappedJobData.getJobDataClass().getName()),
          ex);
    } catch (SerializationException ex) {
      throw new UnserializableJobDataException(
          String.format(
              "Unable to serialize jobData class %s.", wrappedJobData.getJobDataClass().getName()),
          ex);
    }
  }

  private <D> JobData<D> getWrappedJobData(D jobData, SerializationFormat serializationFormat) {
    if (serializationFormat == SerializationFormat.JSON) {
      return new JSONJobData<>(jobData);
    }

    throw new UnserializableJobDataException("Unsupported serialization format");
  }
}
