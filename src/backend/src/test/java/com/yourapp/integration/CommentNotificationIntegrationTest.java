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

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=update")
class CommentNotificationIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void createCommentProducesNotification() {
        Map<String, Object> context = restTemplate.exchange(
                "/api/v1/demo/context",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        ).getBody();
        String projectId = String.valueOf(context.get("projectId"));
        String workflowStateId = String.valueOf(context.get("workflowStateId"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-Team-Role", "member");
        ResponseEntity<Map<String, Object>> issueCreate = restTemplate.exchange(
                "/api/v1/projects/" + projectId + "/issues",
                HttpMethod.POST,
                new HttpEntity<>(Map.of("title", "For comment", "workflowStateId", workflowStateId), headers),
                new ParameterizedTypeReference<>() {}
        );
        String issueId = String.valueOf(issueCreate.getBody().get("id"));

        ResponseEntity<Map<String, Object>> commentCreate = restTemplate.exchange(
                "/api/v1/issues/" + issueId + "/comments",
                HttpMethod.POST,
                new HttpEntity<>(Map.of("body", "hello"), headers),
                new ParameterizedTypeReference<>() {}
        );
        assertThat(commentCreate.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<List<Map<String, Object>>> notifications = restTemplate.exchange(
                "/api/v1/notifications",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        assertThat(notifications.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(notifications.getBody()).isNotEmpty();
    }
}
