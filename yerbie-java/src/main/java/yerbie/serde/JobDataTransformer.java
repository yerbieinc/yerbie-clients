package yerbie.serde;

import yerbie.exception.SerializationException;

public interface JobDataTransformer {
  <D> String serializeJobData(JobData<D> jobData) throws SerializationException;

  <D> JobData<D> deserializeJobData(String rawJobData, Class<D> jobDataClass)
      throws SerializationException;
}
