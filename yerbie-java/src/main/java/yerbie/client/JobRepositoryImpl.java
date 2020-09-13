package yerbie.client;

import java.util.Map;
import java.util.function.Supplier;
import yerbie.exception.JobNotFoundException;
import yerbie.job.Job;

public class JobRepositoryImpl implements JobRepository {
  private final Map<Class, Supplier<Job<?>>> jobDatatoJobSupplierMap;

  public JobRepositoryImpl(Map<Class, Supplier<Job<?>>> jobDatatoJobSupplierMap) {
    this.jobDatatoJobSupplierMap = jobDatatoJobSupplierMap;
  }

  @Override
  public Job findJobForJobData(Object jobData) throws JobNotFoundException {
    if (!(jobDatatoJobSupplierMap.containsKey(jobData.getClass()))) {
      throw new JobNotFoundException(jobData.getClass());
    }

    return jobDatatoJobSupplierMap.get(jobData.getClass()).get();
  }
}
