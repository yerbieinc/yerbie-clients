package yerbie.client;

import yerbie.exception.JobNotFoundException;
import yerbie.job.Job;

public interface JobRepository {
  Job<Object> findJobForJobData(Object jobData) throws JobNotFoundException;
}
