package com.example.backgroundtaskmanager.domain.job;


import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Random;

@Entity
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int min;

    private int max;

    private int count;

    private int currentCounter;

    private String results;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    protected Job() {

    }

    private Job(int min, int max, int count) {

        this.min = min;
        this.max = max;
        this.count = count;
        this.currentCounter = 0;
        this.results = "";
        this.status = JobStatus.PENDING;
    }

    public static Job create(int min, int max, int count) {

        return new Job(min, max, count);
    }

    public void executeStep() {

        if (currentCounter < count) {
            Random random = new Random();
            int randomNumber = random.nextInt(max) + min;
            results += randomNumber + ",";
            currentCounter++;
            status = (currentCounter >= count) ? JobStatus.COMPLETED : JobStatus.RUNNING;
        }
    }

    public Long getId() {

        return id;
    }

    public void setStatus(JobStatus status) {

        this.status = status;
    }
    public JobStatus getStatus() {

        return status;
    }

    public Integer getCurrentCounter() {

        return currentCounter;
    }

    public int getCount() {

        return count;
    }
}