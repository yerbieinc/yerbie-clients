package yerbie.job;

public interface Job<T> {
  void run(T jobData);
}
