package com.yourapp.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
class ApiKeyIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void createListAndDeleteApiKey() {
        ResponseEntity<Map<String, Object>> created = restTemplate.exchange(
                "/api/v1/users/me/api-keys",
                HttpMethod.POST,
                new org.springframework.http.HttpEntity<>(Map.of("label", "integration-key")),
                new ParameterizedTypeReference<>() {}
        );
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(created.getBody()).containsKeys("apiKey", "rawKey");

        ResponseEntity<List<Map<String, Object>>> listed = restTemplate.exchange(
                "/api/v1/users/me/api-keys",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        assertThat(listed.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listed.getBody()).isNotEmpty();

        @SuppressWarnings("unchecked")
        Map<String, Object> apiKey = (Map<String, Object>) created.getBody().get("apiKey");
        String keyId = String.valueOf(apiKey.get("id"));
        ResponseEntity<Void> deleted = restTemplate.exchange(
                "/api/v1/users/me/api-keys/" + keyId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
        assertThat(deleted.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
