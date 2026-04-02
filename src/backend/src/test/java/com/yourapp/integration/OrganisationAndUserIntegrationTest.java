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
class OrganisationAndUserIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void orgTeamAndUserEndpoints() {
        Map<String, Object> ctx = restTemplate.exchange(
                "/api/v1/demo/context",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        ).getBody();

        String orgSlug = String.valueOf(ctx.get("orgSlug"));
        String teamId = String.valueOf(ctx.get("teamId"));

        ResponseEntity<Map<String, Object>> org = restTemplate.exchange(
                "/api/v1/organisations/" + orgSlug,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        assertThat(org.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(org.getBody().get("slug")).isEqualTo(orgSlug);

        ResponseEntity<String> members = restTemplate.exchange(
                "/api/v1/organisations/" + orgSlug + "/members",
                HttpMethod.GET,
                null,
                String.class
        );
        assertThat(members.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(members.getBody()).contains("demo@acme.dev");

        ResponseEntity<List<Map<String, Object>>> teams = restTemplate.exchange(
                "/api/v1/organisations/" + orgSlug + "/teams",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );
        assertThat(teams.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(teams.getBody()).isNotEmpty();
        assertThat(teams.getBody().get(0).get("identifier")).isEqualTo("eng");

        ResponseEntity<Map<String, Object>> teamDetails = restTemplate.exchange(
                "/api/v1/organisations/" + orgSlug + "/teams/" + teamId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        assertThat(teamDetails.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(String.valueOf(teamDetails.getBody().get("id"))).isEqualTo(teamId);

        ResponseEntity<Map<String, Object>> me = restTemplate.exchange(
                "/api/v1/users/me",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        assertThat(me.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(me.getBody().get("email")).isEqualTo("demo@acme.dev");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<Map<String, Object>> updated = restTemplate.exchange(
                "/api/v1/users/me",
                HttpMethod.PATCH,
                new HttpEntity<>(Map.of(
                        "username", "demo2",
                        "timezone", "Europe/London",
                        "avatarUrl", "https://example.com/avatar.png"
                ), headers),
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        assertThat(updated.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updated.getBody().get("username")).isEqualTo("demo2");
        assertThat(updated.getBody().get("timezone")).isEqualTo("Europe/London");
    }
}

