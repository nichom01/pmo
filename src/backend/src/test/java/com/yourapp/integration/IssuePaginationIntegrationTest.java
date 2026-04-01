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

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=update")
class IssuePaginationIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void createAndListIssuesWithCursorEnvelope() {
        ResponseEntity<Map<String, Object>> contextResponse = restTemplate.exchange(
                "/api/v1/demo/context",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        assertThat(contextResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Object> context = contextResponse.getBody();
        assertThat(context).isNotNull();

        String projectId = String.valueOf(context.get("projectId"));
        String workflowStateId = String.valueOf(context.get("workflowStateId"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-Team-Role", "member");
        HttpEntity<Map<String, Object>> createBody = new HttpEntity<>(Map.of(
                "title", "Integration issue",
                "description", "created in integration test",
                "workflowStateId", workflowStateId
        ), headers);

        ResponseEntity<Map<String, Object>> createResponse = restTemplate.exchange(
                "/api/v1/projects/" + projectId + "/issues",
                HttpMethod.POST,
                createBody,
                new ParameterizedTypeReference<>() {}
        );
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<Map<String, Object>> listResponse = restTemplate.exchange(
                "/api/v1/projects/" + projectId + "/issues?limit=1",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Object> payload = listResponse.getBody();
        assertThat(payload).containsKeys("data", "nextCursor", "hasMore");
    }
}
