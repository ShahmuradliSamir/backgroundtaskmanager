package com.example.backgroundtaskmanager.domain.job;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;

class JobTest {

    @Test
    void shouldExecuteStepAndIncreaseCounter() {

        Job job = Job.create(1, 10, 3);

        job.executeStep();
        assertThat(job.getCurrentCounter()).isEqualTo(1);
        assertThat(job.getStatus()).isEqualTo(JobStatus.RUNNING);

        job.executeStep();
        assertThat(job.getCurrentCounter()).isEqualTo(2);

        job.executeStep();
        assertThat(job.getCurrentCounter()).isEqualTo(3);
        assertThat(job.getStatus()).isEqualTo(JobStatus.COMPLETED);
    }

    @Test
    void shouldNotExecuteStepWhenCompleted() {

        Job job = Job.create(1, 10, 2);
        job.executeStep();
        job.executeStep();

        assertThat(job.getStatus()).isEqualTo(JobStatus.COMPLETED);

        int counterBefore = job.getCurrentCounter();
        job.executeStep();
        assertThat(job.getCurrentCounter()).isEqualTo(counterBefore);
    }
}