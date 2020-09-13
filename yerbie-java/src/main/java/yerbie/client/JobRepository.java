package yerbie.client;

import yerbie.job.Job;

public interface JobRepository {
  Job<Object> findJobForJobData(Object jobData);
}
