package com.example.integration;

import com.example.entity.AppUser;
import com.example.entity.Role;
import com.example.entity.UserGroup;
import com.example.repository.RoleRepository;
import com.example.repository.UserGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserApiIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private com.example.repository.UserRepository userRepository;

    @Test
    @DisplayName("End-to-End: Full User Lifecycle (Create, Read, Update Roles, Groups, Delete)")
    void testUserLifecycle() {
        // 1. Create a new user
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String createJson = """
                {
                    "email": "e2e_user@example.com",
                    "password": "Password@123",
                    "confirmPassword": "Password@123",
                    "firstName": "Integration",
                    "lastName": "Tester"
                }
                """;

        HttpEntity<String> createRequest = new HttpEntity<>(createJson, headers);
        ResponseEntity<AppUser> createResponse = restTemplate.postForEntity(
                "/api/v1/users", createRequest, AppUser.class);

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        Long createdUserId = createResponse.getBody().getUserId();
        assertNotNull(createdUserId);
        assertEquals("e2e_user@example.com", createResponse.getBody().getEmail());

        // 2. Fetch created user by ID
        ResponseEntity<AppUser> getResponse = restTemplate.getForEntity(
                "/api/v1/users/" + createdUserId, AppUser.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals("Integration", getResponse.getBody().getFirstName());

        // 3. Assign Role to User
        Role adminRole = roleRepository.findAll().stream()
                .filter(r -> "ROLE_ADMIN".equals(r.getRoleName()))
                .findFirst()
                .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN", "Admin")));

        HttpEntity<Set<Role>> assignRoleRequest = new HttpEntity<>(Set.of(adminRole), headers);
        ResponseEntity<AppUser> assignResponse = restTemplate.exchange(
                "/api/v1/users/" + createdUserId + "/roles",
                HttpMethod.PUT,
                assignRoleRequest,
                AppUser.class
        );
        assertEquals(HttpStatus.OK, assignResponse.getStatusCode());

        // 4. Fetch User Roles
        ResponseEntity<Role[]> userRolesResponse = restTemplate.getForEntity(
                "/api/v1/users/" + createdUserId + "/roles", Role[].class);
        assertEquals(HttpStatus.OK, userRolesResponse.getStatusCode());
        assertThat(userRolesResponse.getBody()).isNotEmpty();

        // 4b. Remove Role from User and re-assign
        HttpEntity<Set<Role>> removeRoleRequest = new HttpEntity<>(Set.of(adminRole), headers);
        ResponseEntity<AppUser> removeResponse = restTemplate.exchange(
                "/api/v1/users/" + createdUserId + "/roles",
                HttpMethod.DELETE,
                removeRoleRequest,
                AppUser.class
        );
        assertEquals(HttpStatus.OK, removeResponse.getStatusCode());

        // Re-assign role
        restTemplate.exchange(
                "/api/v1/users/" + createdUserId + "/roles",
                HttpMethod.PUT,
                assignRoleRequest,
                AppUser.class
        );

        // 5. Add User to Group
        UserGroup testGroup = userGroupRepository.findAll().stream()
                .findFirst()
                .orElseGet(() -> userGroupRepository.save(new UserGroup("TestGroup", "Testing")));

        ResponseEntity<Void> addGroupResponse = restTemplate.postForEntity(
                "/api/v1/users/" + createdUserId + "/groups/" + testGroup.getUserGroupId(),
                null,
                Void.class
        );
        assertEquals(HttpStatus.OK, addGroupResponse.getStatusCode());

        // 6. Fetch User Groups
        ResponseEntity<UserGroup[]> userGroupsResponse = restTemplate.getForEntity(
                "/api/v1/users/" + createdUserId + "/groups", UserGroup[].class);
        assertEquals(HttpStatus.OK, userGroupsResponse.getStatusCode());
        assertThat(userGroupsResponse.getBody()).isNotEmpty();

        // 7. Remove User from Group
        ResponseEntity<Void> removeGroupResponse = restTemplate.exchange(
                "/api/v1/users/" + createdUserId + "/groups/" + testGroup.getUserGroupId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );
        assertEquals(HttpStatus.OK, removeGroupResponse.getStatusCode());

        // 8. Delete User
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "/api/v1/users/" + createdUserId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());

        // 9. Verify User Not Found (404)
        ResponseEntity<Map> notFoundResponse = restTemplate.getForEntity(
                "/api/v1/users/" + createdUserId, Map.class);
        assertEquals(HttpStatus.NOT_FOUND, notFoundResponse.getStatusCode());
    }

    @Test
    @DisplayName("End-to-End: Should return 400 with validation errors on invalid user payload")
    void testCreateUserValidationErrors() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String invalidJson = """
                {
                    "email": "invalid-email-address",
                    "password": "weak",
                    "confirmPassword": "mismatch",
                    "firstName": "",
                    "lastName": ""
                }
                """;

        HttpEntity<String> request = new HttpEntity<>(invalidJson, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/v1/users", request, Map.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertThat(response.getBody()).containsKey("errors");
    }

    @Test
    @DisplayName("End-to-End: Should fetch users count and paginated users")
    void testGetUsersCountAndPagination() {
        ResponseEntity<Long> countResponse = restTemplate.getForEntity(
                "/api/v1/users/count", Long.class);
        assertEquals(HttpStatus.OK, countResponse.getStatusCode());
        assertThat(countResponse.getBody()).isGreaterThanOrEqualTo(0L);

        ResponseEntity<Map> pagedResponse = restTemplate.getForEntity(
                "/api/v1/users?page=0&size=5", Map.class);
        assertEquals(HttpStatus.OK, pagedResponse.getStatusCode());
        assertThat(pagedResponse.getBody()).containsKey("content");
    }

    @Test
    @DisplayName("Should delete user associated with roles and groups without referential integrity violation")
    void testDeleteUserAssociatedWithRolesAndGroups() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 1. Create a user via API
        String email = "user_cascade_" + System.currentTimeMillis() + "@example.com";
        String userJson = String.format("""
                {
                    "email": "%s",
                    "password": "Password@123",
                    "confirmPassword": "Password@123",
                    "firstName": "First",
                    "lastName": "Last"
                }
                """, email);
        HttpEntity<String> createUserRequest = new HttpEntity<>(userJson, headers);
        ResponseEntity<AppUser> userResponse = restTemplate.postForEntity(
                "/api/v1/users", createUserRequest, AppUser.class);
        assertEquals(HttpStatus.CREATED, userResponse.getStatusCode());
        Long userId = userResponse.getBody().getUserId();

        // 2. Assign Role via API
        Role role = roleRepository.findAll().stream().findFirst().orElseThrow();
        HttpEntity<Set<Role>> assignRoleRequest = new HttpEntity<>(Set.of(role), headers);
        ResponseEntity<AppUser> assignResponse = restTemplate.exchange(
                "/api/v1/users/" + userId + "/roles",
                HttpMethod.PUT,
                assignRoleRequest,
                AppUser.class
        );
        assertEquals(HttpStatus.OK, assignResponse.getStatusCode());

        // 3. Add to Group via API
        UserGroup group = userGroupRepository.findAll().stream().findFirst().orElseThrow();
        ResponseEntity<Void> addGroupResponse = restTemplate.postForEntity(
                "/api/v1/users/" + userId + "/groups/" + group.getUserGroupId(),
                null,
                Void.class
        );
        assertEquals(HttpStatus.OK, addGroupResponse.getStatusCode());

        // 4. Delete user directly via API without manually removing from role or group first
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "/api/v1/users/" + userId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());

        // 5. Verify user is deleted (404)
        ResponseEntity<Map> notFoundResponse = restTemplate.getForEntity(
                "/api/v1/users/" + userId, Map.class);
        assertEquals(HttpStatus.NOT_FOUND, notFoundResponse.getStatusCode());
    }
}
