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
  public Job findJobForJobClass(Class jobClass) throws JobNotFoundException {
    if (!(jobDatatoJobSupplierMap.containsKey(jobClass))) {
      throw new JobNotFoundException(jobClass);
    }

    return jobDatatoJobSupplierMap.get(jobClass).get();
  }
}
