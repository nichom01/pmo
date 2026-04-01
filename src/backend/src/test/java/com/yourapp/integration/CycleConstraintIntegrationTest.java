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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=update")
class CycleConstraintIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void onlyOneActiveCyclePerProject() {
        String projectId = String.valueOf(getContext().get("projectId"));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-Team-Role", "member");

        ResponseEntity<Map<String, Object>> c1 = restTemplate.exchange(
                "/api/v1/projects/" + projectId + "/cycles",
                HttpMethod.POST,
                new HttpEntity<>(Map.of("name", "Cycle A", "startDate", LocalDate.now().toString(), "endDate", LocalDate.now().plusDays(7).toString()), headers),
                new ParameterizedTypeReference<>() {}
        );
        ResponseEntity<Map<String, Object>> c2 = restTemplate.exchange(
                "/api/v1/projects/" + projectId + "/cycles",
                HttpMethod.POST,
                new HttpEntity<>(Map.of("name", "Cycle B", "startDate", LocalDate.now().toString(), "endDate", LocalDate.now().plusDays(7).toString()), headers),
                new ParameterizedTypeReference<>() {}
        );
        assertThat(c1.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(c2.getStatusCode()).isEqualTo(HttpStatus.OK);

        String cycle1Id = String.valueOf(c1.getBody().get("id"));
        String cycle2Id = String.valueOf(c2.getBody().get("id"));

        ResponseEntity<Map<String, Object>> activate1 = restTemplate.exchange(
                "/api/v1/cycles/" + cycle1Id,
                HttpMethod.PATCH,
                new HttpEntity<>(Map.of("status", "active"), headers),
                new ParameterizedTypeReference<>() {}
        );
        assertThat(activate1.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<Map<String, Object>> activate2 = restTemplate.exchange(
                "/api/v1/cycles/" + cycle2Id,
                HttpMethod.PATCH,
                new HttpEntity<>(Map.of("status", "active"), headers),
                new ParameterizedTypeReference<>() {}
        );
        assertThat(activate2.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private Map<String, Object> getContext() {
        ResponseEntity<Map<String, Object>> contextResponse = restTemplate.exchange(
                "/api/v1/demo/context",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        return contextResponse.getBody();
    }
}
