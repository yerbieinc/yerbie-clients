package yerbie.client;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import yerbie.job.Job;

public class JobRepositoryBuilder {
  private final Map<String, Supplier<Job<?>>> jobDatatoJobSupplierMap;

  public JobRepositoryBuilder() {
    jobDatatoJobSupplierMap = new HashMap<>();
  }

  public JobRepositoryBuilder(Class<?> jobDataClass, Supplier<Job<?>> jobSupplier) {
    jobDatatoJobSupplierMap = Map.of(jobDataClass.getName(), jobSupplier);
  }

  public JobRepository build() {
    return new JobRepositoryImpl(jobDatatoJobSupplierMap);
  }

  public JobRepositoryBuilder withJobData(Class<?> jobDataClass, Supplier<Job<?>> jobSupplier) {
    jobDatatoJobSupplierMap.put(jobDataClass.getName(), jobSupplier);
    return this;
  }
}
