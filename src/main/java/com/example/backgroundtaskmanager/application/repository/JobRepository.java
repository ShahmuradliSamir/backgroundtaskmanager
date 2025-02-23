package com.example.backgroundtaskmanager.application.repository;


import com.example.backgroundtaskmanager.domain.job.Job;
import com.example.backgroundtaskmanager.domain.job.JobStatus;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface JobRepository extends CrudRepository<Job, Long> {

    Optional<Job> findByMinAndMaxAndCountAndStatus(int min, int max, int count, JobStatus status);

    Iterable<Job> findByStatus(JobStatus status);
}