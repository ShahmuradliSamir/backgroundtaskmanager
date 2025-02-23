package com.example.backgroundtaskmanager.steps;

import com.example.backgroundtaskmanager.application.dto.JobRequest;
import com.example.backgroundtaskmanager.application.dto.JobResponse;
import com.example.backgroundtaskmanager.domain.job.Job;
import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JobStepDefinitions {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private Long lastJobId;
    private JobResponse lastJobResponse;
    private ResponseEntity<Job> jobResponseEntity;

    @Given("я создаю задачу с параметрами min={int}, max={int}, count={int}")
    public void createJob(int min, int max, int count) {
        String url = "http://localhost:" + port + "/jobs";
        JobRequest request = new JobRequest(min, max, count);
        ResponseEntity<JobResponse> response = restTemplate.postForEntity(url, request, JobResponse.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        lastJobResponse = response.getBody();
        Assertions.assertNotNull(lastJobResponse);
        lastJobId = lastJobResponse.jobId();
    }

    @Then("ответ содержит jobId")
    public void checkJobId() {

        Assertions.assertNotNull(lastJobId);
    }

    @Then("задача имеет статус {string}")
    public void checkJobStatus(String expectedStatus) {
        String url = "http://localhost:" + port + "/jobs/" + lastJobId;
        ResponseEntity<Job> response = restTemplate.getForEntity(url, Job.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Job job = response.getBody();
        Assertions.assertNotNull(job);
        Assertions.assertEquals(expectedStatus, job.getStatus().name());
    }

    @Given("существует задача с параметрами min={int}, max={int}, count={int}")
    public void givenExistingJob(int min, int max, int count) {
        createJob(min, max, count);
    }

    @When("я запрашиваю информацию о задаче")
    public void requestJobInfo() {
        String url = "http://localhost:" + port + "/jobs/" + lastJobId;
        jobResponseEntity = restTemplate.getForEntity(url, Job.class);
        Assertions.assertEquals(HttpStatus.OK, jobResponseEntity.getStatusCode());
    }

    @Then("я получаю статус {string}")
    public void thenJobStatus(String expectedStatus) {
        Job job = jobResponseEntity.getBody();
        Assertions.assertNotNull(job);
        Assertions.assertEquals(expectedStatus, job.getStatus().name());
    }

    @When("я создаю задачу снова с теми же параметрами")
    public void createDuplicateJob() {
        createJob(1, 10, 3);
    }

    @Then("я получаю тот же jobId")
    public void checkDuplicateJobId() {
        Assertions.assertNotNull(lastJobResponse);
        Long duplicateJobId = lastJobResponse.jobId();
        Assertions.assertEquals(lastJobId, duplicateJobId, "JobId должен быть одинаковым для повторного создания");
    }
}