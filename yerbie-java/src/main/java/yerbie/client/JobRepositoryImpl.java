package yerbie.client;

import java.util.Map;
import java.util.function.Supplier;
import yerbie.job.Job;

public class JobRepositoryImpl implements JobRepository {
  private final Map<String, Supplier<Job<?>>> jobDatatoJobSupplierMap;

  public JobRepositoryImpl(Map<String, Supplier<Job<?>>> jobDatatoJobSupplierMap) {
    this.jobDatatoJobSupplierMap = jobDatatoJobSupplierMap;
  }

  @Override
  public Job findJobForJobData(Object jobData) {
    System.out.println("finding job for " + jobData.getClass().getName());

    if (!(jobDatatoJobSupplierMap.containsKey(jobData.getClass().getName()))) {
      // throw exception
      System.out.println("Could not find job!!!");
      return null;
    }

    return jobDatatoJobSupplierMap.get(jobData.getClass().getName()).get();
  }
}
