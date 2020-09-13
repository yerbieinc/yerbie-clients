package yerbie.client;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import yerbie.job.Job;

public class JobRepositoryBuilder {
  private final Map<Class, Supplier<Job<?>>> jobDatatoJobSupplierMap;

  public JobRepositoryBuilder() {
    jobDatatoJobSupplierMap = new HashMap<>();
  }

  // Map is immutable, but since this is a builder we want the underlying map to be
  // mutable. Hence converting it into a HashMap.
  public JobRepositoryBuilder(Class<?> jobDataClass, Supplier<Job<?>> jobSupplier) {
    jobDatatoJobSupplierMap = new HashMap<>(Map.of(jobDataClass, jobSupplier));
  }

  public JobRepository build() {
    return new JobRepositoryImpl(jobDatatoJobSupplierMap);
  }

  public JobRepositoryBuilder withJobData(Class<?> jobDataClass, Supplier<Job<?>> jobSupplier) {
    jobDatatoJobSupplierMap.put(jobDataClass, jobSupplier);
    return this;
  }
}
