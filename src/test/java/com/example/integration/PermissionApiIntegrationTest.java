package com.example.integration;

import com.example.entity.Permission;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PermissionApiIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("End-to-End: Full Permission Lifecycle (Create, Read, List, Roles, Delete)")
    void testPermissionLifecycle() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 1. Create permission
        Permission newPerm = new Permission("PERM_" + System.currentTimeMillis(), "Custom Permission");
        HttpEntity<Permission> createRequest = new HttpEntity<>(newPerm, headers);

        ResponseEntity<Permission> createResponse = restTemplate.postForEntity(
                "/api/v1/permissions", createRequest, Permission.class);

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        Long permId = createResponse.getBody().getPermissionId();
        assertNotNull(permId);

        // 2. Fetch permission by ID
        ResponseEntity<Permission> getResponse = restTemplate.getForEntity(
                "/api/v1/permissions/" + permId, Permission.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals(newPerm.getPermissionName(), getResponse.getBody().getPermissionName());

        // 3. List all permissions paginated
        ResponseEntity<Map> listResponse = restTemplate.getForEntity(
                "/api/v1/permissions?page=0&size=10", Map.class);
        assertEquals(HttpStatus.OK, listResponse.getStatusCode());
        assertThat(listResponse.getBody()).containsKey("content");

        // 4. List roles by permission
        ResponseEntity<Map> rolesResponse = restTemplate.getForEntity(
                "/api/v1/permissions/" + permId + "/roles?page=0&size=10", Map.class);
        assertEquals(HttpStatus.OK, rolesResponse.getStatusCode());
        assertThat(rolesResponse.getBody()).containsKey("content");

        // 5. Delete permission
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "/api/v1/permissions/" + permId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());

        // 6. Verify permission not found
        ResponseEntity<Map> notFoundResponse = restTemplate.getForEntity(
                "/api/v1/permissions/" + permId, Map.class);
        assertEquals(HttpStatus.NOT_FOUND, notFoundResponse.getStatusCode());
    }

    @Test
    @DisplayName("Should delete permission associated with roles without referential integrity violation")
    void testDeletePermissionAssociatedWithRole() {
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "/api/v1/permissions/2",
                HttpMethod.DELETE,
                null,
                Void.class
        );
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());

        ResponseEntity<Map> notFoundResponse = restTemplate.getForEntity(
                "/api/v1/permissions/2", Map.class);
        assertEquals(HttpStatus.NOT_FOUND, notFoundResponse.getStatusCode());
    }
}
