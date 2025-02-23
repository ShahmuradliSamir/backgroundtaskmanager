package com.example.backgroundtaskmanager.application.controller;

import com.example.backgroundtaskmanager.application.dto.JobRequest;
import com.example.backgroundtaskmanager.application.dto.JobResponse;
import com.example.backgroundtaskmanager.application.service.JobApplicationService;
import com.example.backgroundtaskmanager.domain.job.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jobs")
public class JobController {

    private final JobApplicationService jobApplicationService;

    @Autowired
    public JobController(JobApplicationService jobApplicationService) {

        this.jobApplicationService = jobApplicationService;
    }

    @PostMapping
    public ResponseEntity<JobResponse> startJob(@RequestBody JobRequest request) {

        Long jobId = jobApplicationService.startJob(request.min(), request.max(), request.count());
        return ResponseEntity.ok(new JobResponse(jobId));
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<Job> getJob(@PathVariable Long jobId) {

        return jobApplicationService.getJob(jobId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}