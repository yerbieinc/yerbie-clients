package yerbie.client;

import yerbie.exception.JobNotFoundException;
import yerbie.job.Job;

public interface JobRepository {
  Job<Object> findJobForJobClass(Class jobData) throws JobNotFoundException;
}
