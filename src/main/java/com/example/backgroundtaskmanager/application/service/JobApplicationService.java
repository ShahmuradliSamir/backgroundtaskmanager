package com.example.backgroundtaskmanager.application.service;

import com.example.backgroundtaskmanager.application.repository.JobRepository;
import com.example.backgroundtaskmanager.domain.job.Job;
import com.example.backgroundtaskmanager.domain.job.JobStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.Optional;
import jakarta.annotation.PostConstruct;
import java.util.concurrent.CompletableFuture;

@Service
public class JobApplicationService {

    private final JobRepository jobRepository;

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public JobApplicationService(JobRepository jobRepository, StringRedisTemplate redisTemplate) {

        this.jobRepository = jobRepository;
        this.redisTemplate = redisTemplate;
    }

    public Long startJob(int min, int max, int count) {

        String lockKey = "job:" + min + ":" + max + ":" + count;

        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "LOCKED", Duration.ofSeconds(10));

        if (Boolean.FALSE.equals(locked)) {
            Optional<Job> existingJob = jobRepository.findByMinAndMaxAndCountAndStatus(min, max, count,
                JobStatus.RUNNING);
            return existingJob.map(Job::getId).orElseThrow(() -> new RuntimeException("Ошибка получения задачи"));
        }

        try {
            Optional<Job> existingJob = jobRepository.findByMinAndMaxAndCountAndStatus(min, max, count,
                JobStatus.RUNNING);
            if (existingJob.isPresent()) {
                return existingJob.get().getId();
            }

            Job job = Job.create(min, max, count);
            job.setStatus(JobStatus.RUNNING);
            jobRepository.save(job);

            CompletableFuture.runAsync(() -> runJob(job));
            return job.getId();
        } finally {
            redisTemplate.delete(lockKey);
        }
    }

    public void runJob(Job job) {

        while (job.getCurrentCounter() < job.getCount()) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                job.setStatus(JobStatus.FAILED);
                jobRepository.save(job);
                return;
            }

            job.executeStep();
            jobRepository.save(job);
        }

        job.setStatus(JobStatus.COMPLETED);
        jobRepository.save(job);
    }

    public Optional<Job> getJob(Long jobId) {

        return jobRepository.findById(jobId);
    }

    @PostConstruct
    /*
    С h2 этот метод не нужен, так как база данных всегда пуста при старте.
     */
    public void resumeJobs() {

        var runningJobs = jobRepository.findByStatus(JobStatus.RUNNING);
        for (Job job : runningJobs) {
            CompletableFuture.runAsync(() -> runJob(job));
        }
    }
}
