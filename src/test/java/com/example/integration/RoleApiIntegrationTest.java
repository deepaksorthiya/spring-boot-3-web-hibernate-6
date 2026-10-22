package com.example.integration;

import com.example.entity.Role;
import com.example.repository.PermissionRepository;
import com.example.repository.RoleRepository;
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
class RoleApiIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Test
    @DisplayName("End-to-End: Full Role Lifecycle (Create, Read, List, Find Users, Delete)")
    void testRoleLifecycle() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 1. Create a new role
        Role newRole = new Role("ROLE_TESTER_" + System.currentTimeMillis(), "Software Tester Role");
        HttpEntity<Role> createRequest = new HttpEntity<>(newRole, headers);

        ResponseEntity<Role> createResponse = restTemplate.postForEntity(
                "/api/v1/roles", createRequest, Role.class);

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        Long createdRoleId = createResponse.getBody().getRoleId();
        assertNotNull(createdRoleId);

        // 2. Fetch created role by ID
        ResponseEntity<Role> getResponse = restTemplate.getForEntity(
                "/api/v1/roles/" + createdRoleId, Role.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals(newRole.getRoleName(), getResponse.getBody().getRoleName());

        // 3. Get all roles with permissions (paginated)
        ResponseEntity<Map> listResponse = restTemplate.getForEntity(
                "/api/v1/roles?page=0&size=10", Map.class);
        assertEquals(HttpStatus.OK, listResponse.getStatusCode());
        assertThat(listResponse.getBody()).containsKey("content");

        // 4. Find all users for role
        ResponseEntity<Map> usersResponse = restTemplate.getForEntity(
                "/api/v1/roles/" + createdRoleId + "/users?page=0&size=10", Map.class);
        assertEquals(HttpStatus.OK, usersResponse.getStatusCode());
        assertThat(usersResponse.getBody()).containsKey("content");

        // 5. Delete role
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "/api/v1/roles/" + createdRoleId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());

        // 6. Verify role not found
        ResponseEntity<Map> notFoundResponse = restTemplate.getForEntity(
                "/api/v1/roles/" + createdRoleId, Map.class);
        assertEquals(HttpStatus.NOT_FOUND, notFoundResponse.getStatusCode());
    }
}
