package com.example.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ActuatorIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Actuator: /actuator/health should return status UP")
    void testActuatorHealth() {
        ResponseEntity<Map> response = restTemplate.getForEntity("/actuator/health", Map.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).containsKey("status");
        assertEquals("UP", response.getBody().get("status"));
    }

    @Test
    @DisplayName("Actuator: /actuator should expose management endpoints")
    void testActuatorRoot() {
        ResponseEntity<Map> response = restTemplate.getForEntity("/actuator", Map.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).containsKey("_links");
    }
}
