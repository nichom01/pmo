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
class AttachmentIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void createAndListAttachment() {
        Map<String, Object> ctx = restTemplate.exchange("/api/v1/demo/context", HttpMethod.GET, null, new ParameterizedTypeReference<Map<String, Object>>() {}).getBody();
        String projectId = String.valueOf(ctx.get("projectId"));
        String workflowStateId = String.valueOf(ctx.get("workflowStateId"));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-Team-Role", "member");

        String issueId = String.valueOf(restTemplate.exchange(
                "/api/v1/projects/" + projectId + "/issues",
                HttpMethod.POST,
                new HttpEntity<>(Map.of("title", "Attachment issue", "workflowStateId", workflowStateId), headers),
                new ParameterizedTypeReference<Map<String, Object>>() {}
        ).getBody().get("id"));

        ResponseEntity<Map<String, Object>> created = restTemplate.exchange(
                "/api/v1/issues/" + issueId + "/attachments",
                HttpMethod.POST,
                new HttpEntity<>(Map.of("filename", "test.txt", "content", "hello", "mimeType", "text/plain"), headers),
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.OK);
        String attachmentId = String.valueOf(created.getBody().get("id"));

        ResponseEntity<List<Map<String, Object>>> listed = restTemplate.exchange(
                "/api/v1/issues/" + issueId + "/attachments",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );
        assertThat(listed.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listed.getBody()).isNotEmpty();

        ResponseEntity<Void> deleted = restTemplate.exchange(
                "/api/v1/attachments/" + attachmentId,
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class
        );
        assertThat(deleted.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<List<Map<String, Object>>> listedAfterDelete = restTemplate.exchange(
                "/api/v1/issues/" + issueId + "/attachments",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );
        assertThat(listedAfterDelete.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listedAfterDelete.getBody()).isEmpty();
    }
}
