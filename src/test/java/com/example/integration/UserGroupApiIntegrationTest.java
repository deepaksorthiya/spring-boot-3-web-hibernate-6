package com.example.integration;

import com.example.entity.AppUser;
import com.example.entity.UserGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserGroupApiIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("End-to-End: Full UserGroup Lifecycle (Create, Read, Update, With Users, Delete)")
    void testUserGroupLifecycle() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 1. Create a user group
        UserGroup newGroup = new UserGroup("Finance_" + System.currentTimeMillis(), "Finance Department");
        HttpEntity<UserGroup> createRequest = new HttpEntity<>(newGroup, headers);

        ResponseEntity<UserGroup> createResponse = restTemplate.postForEntity(
                "/api/v1/groups", createRequest, UserGroup.class);
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

        // 2. Fetch all groups
        ResponseEntity<UserGroup[]> allGroupsResponse = restTemplate.getForEntity(
                "/api/v1/groups", UserGroup[].class);
        assertEquals(HttpStatus.OK, allGroupsResponse.getStatusCode());
        assertThat(allGroupsResponse.getBody()).isNotEmpty();

        UserGroup createdGroup = null;
        for (UserGroup g : allGroupsResponse.getBody()) {
            if (newGroup.getGroupName().equals(g.getGroupName())) {
                createdGroup = g;
                break;
            }
        }
        assertThat(createdGroup).isNotNull();
        Long groupId = createdGroup.getUserGroupId();

        // 3. Fetch group by ID
        ResponseEntity<UserGroup> getResponse = restTemplate.getForEntity(
                "/api/v1/groups/" + groupId, UserGroup.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals(newGroup.getGroupName(), getResponse.getBody().getGroupName());

        // 4. Fetch group with users by ID
        ResponseEntity<UserGroup> withUsersResponse = restTemplate.getForEntity(
                "/api/v1/groups/" + groupId + "/with-users", UserGroup.class);
        assertEquals(HttpStatus.OK, withUsersResponse.getStatusCode());

        // 5. Fetch group users
        ResponseEntity<AppUser[]> usersResponse = restTemplate.getForEntity(
                "/api/v1/groups/" + groupId + "/users", AppUser[].class);
        assertEquals(HttpStatus.OK, usersResponse.getStatusCode());

        // 6. Fetch all groups with users
        ResponseEntity<UserGroup[]> allWithUsersResponse = restTemplate.getForEntity(
                "/api/v1/groups/with-users", UserGroup[].class);
        assertEquals(HttpStatus.OK, allWithUsersResponse.getStatusCode());

        // 7. Update group
        createdGroup.setDescription("Updated description");
        HttpEntity<UserGroup> updateRequest = new HttpEntity<>(createdGroup, headers);
        ResponseEntity<UserGroup> updateResponse = restTemplate.exchange(
                "/api/v1/groups/" + groupId,
                HttpMethod.PUT,
                updateRequest,
                UserGroup.class
        );
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());

        // 8. Delete group
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "/api/v1/groups/" + groupId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        // 9. Verify group deleted (404)
        ResponseEntity<Map> notFoundResponse = restTemplate.getForEntity(
                "/api/v1/groups/" + groupId, Map.class);
        assertEquals(HttpStatus.NOT_FOUND, notFoundResponse.getStatusCode());
    }
}
