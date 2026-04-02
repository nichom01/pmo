package com.yourapp.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=update")
class ProjectIssueCycleCRUDIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void projectIssueAndCycleCRUD() {
        Map<String, Object> ctx = restTemplate.exchange(
                "/api/v1/demo/context",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        ).getBody();

        String projectId = String.valueOf(ctx.get("projectId"));
        String workflowStateId = String.valueOf(ctx.get("workflowStateId"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-Team-Role", "member");

        // Create issue
        ResponseEntity<Map<String, Object>> createdIssue = restTemplate.exchange(
                "/api/v1/projects/" + projectId + "/issues",
                HttpMethod.POST,
                new HttpEntity<>(Map.of(
                        "title", "Create issue",
                        "description", "initial description",
                        "workflowStateId", workflowStateId
                ), headers),
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        assertThat(createdIssue.getStatusCode()).isEqualTo(HttpStatus.OK);
        String issueId = String.valueOf(createdIssue.getBody().get("id"));

        // Get issue
        ResponseEntity<Map<String, Object>> issue = restTemplate.exchange(
                "/api/v1/issues/" + issueId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        assertThat(issue.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(issue.getBody().get("title")).isEqualTo("Create issue");

        // Update issue
        ResponseEntity<Map<String, Object>> updatedIssue = restTemplate.exchange(
                "/api/v1/issues/" + issueId,
                HttpMethod.PATCH,
                new HttpEntity<>(Map.of(
                        "title", "Updated issue",
                        "description", "updated description",
                        "workflowStateId", workflowStateId,
                        "priority", "high"
                ), headers),
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        assertThat(updatedIssue.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updatedIssue.getBody().get("title")).isEqualTo("Updated issue");

        // Delete issue (soft delete)
        ResponseEntity<Void> deletedIssue = restTemplate.exchange(
                "/api/v1/issues/" + issueId,
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class
        );
        assertThat(deletedIssue.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<Map<String, Object>> issueAfterDelete = restTemplate.exchange(
                "/api/v1/issues/" + issueId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        assertThat(issueAfterDelete.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        // Project: get/update/delete (soft)
        ResponseEntity<Map<String, Object>> project = restTemplate.exchange(
                "/api/v1/projects/" + projectId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        assertThat(project.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(project.getBody().get("id")).isEqualTo(projectId);

        ResponseEntity<Map<String, Object>> updatedProject = restTemplate.exchange(
                "/api/v1/projects/" + projectId,
                HttpMethod.PATCH,
                new HttpEntity<>(Map.of(
                        "name", "PMO Core Updated",
                        "description", "updated description",
                        "status", "completed"
                ), headers),
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        assertThat(updatedProject.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updatedProject.getBody().get("name")).isEqualTo("PMO Core Updated");

        // Cycle: create + get detail (must happen before project soft delete)
        Map<String, Object> createCyclePayload = new HashMap<>();
        createCyclePayload.put("name", "Cycle 1");
        createCyclePayload.put("description", null);
        createCyclePayload.put("startDate", "2026-01-01");
        createCyclePayload.put("endDate", "2026-01-08");
        ResponseEntity<Map<String, Object>> createdCycle = restTemplate.exchange(
                "/api/v1/projects/" + projectId + "/cycles",
                HttpMethod.POST,
                new HttpEntity<>(createCyclePayload, headers),
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        assertThat(createdCycle.getStatusCode()).isEqualTo(HttpStatus.OK);
        String cycleId = String.valueOf(createdCycle.getBody().get("id"));

        ResponseEntity<Map<String, Object>> cycle = restTemplate.exchange(
                "/api/v1/cycles/" + cycleId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        assertThat(cycle.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(cycle.getBody().get("status")).isEqualTo("draft");

        ResponseEntity<Void> deletedProject = restTemplate.exchange(
                "/api/v1/projects/" + projectId,
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class
        );
        assertThat(deletedProject.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<Map<String, Object>> projectAfterDelete = restTemplate.exchange(
                "/api/v1/projects/" + projectId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        assertThat(projectAfterDelete.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}

