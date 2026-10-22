package com.example.controller;

import com.example.config.WebAppConfig;
import com.example.entity.AppUser;
import com.example.entity.Role;
import com.example.entity.UserGroup;
import com.example.global.exceptions.ResourceNotFoundException;
import com.example.global.exceptions.UserControllerAdviceHandler;
import com.example.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({WebAppConfig.class, UserControllerAdviceHandler.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private AppUser sampleUser;
    private Role sampleRole;
    private UserGroup sampleGroup;

    @BeforeEach
    void setUp() {
        sampleUser = new AppUser("john@example.com", "Password@123", "Password@123", "John", "Doe");
        sampleUser.setUserId(1L);

        sampleRole = new Role("ROLE_USER", "Regular User");
        sampleRole.setRoleId(10L);

        sampleGroup = new UserGroup("Developers", "Dev Group");
        sampleGroup.setUserGroupId(100L);
    }

    @Test
    @DisplayName("POST /api/v1/users - Should create user when input is valid")
    void testCreateAppUser_Success() throws Exception {
        when(userService.createUser(any(AppUser.class))).thenReturn(sampleUser);

        String userJson = """
                {
                    "email": "john@example.com",
                    "password": "Password@123",
                    "confirmPassword": "Password@123",
                    "firstName": "John",
                    "lastName": "Doe"
                }
                """;

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));

        verify(userService, times(1)).createUser(any(AppUser.class));
    }

    @Test
    @DisplayName("POST /api/v1/users - Should fail validation and return 400 when invalid email or weak password")
    void testCreateAppUser_ValidationFailure() throws Exception {
        String invalidUserJson = """
                {
                    "email": "invalid-email",
                    "password": "weak",
                    "confirmPassword": "mismatch",
                    "firstName": "",
                    "lastName": ""
                }
                """;

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidUserJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());

        verify(userService, never()).createUser(any());
    }

    @Test
    @DisplayName("DELETE /api/v1/users/{userId} - Should delete user by ID")
    void testDeleteAppUserById() throws Exception {
        doNothing().when(userService).deleteAppUserById(1L);

        mockMvc.perform(delete("/api/v1/users/{userId}", 1L))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteAppUserById(1L);
    }

    @Test
    @DisplayName("GET /api/v1/users/{userId} - Should return user by ID when found")
    void testGetAppUserById_Success() throws Exception {
        when(userService.getAppUserById(1L)).thenReturn(sampleUser);

        mockMvc.perform(get("/api/v1/users/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.email").value("john@example.com"));

        verify(userService, times(1)).getAppUserById(1L);
    }

    @Test
    @DisplayName("GET /api/v1/users/{userId} - Should return 404 when user not found")
    void testGetAppUserById_NotFound() throws Exception {
        when(userService.getAppUserById(99L)).thenThrow(new ResourceNotFoundException(99L));

        mockMvc.perform(get("/api/v1/users/{userId}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value(containsString("/api/v1/users/99")));

        verify(userService, times(1)).getAppUserById(99L);
    }

    @Test
    @DisplayName("GET /api/v1/users - Should return paged users")
    void testFindAll() throws Exception {
        Page<AppUser> page = new PageImpl<>(List.of(sampleUser), PageRequest.of(0, 10), 1);
        when(userService.findAll(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/users?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].email").value("john@example.com"));

        verify(userService, times(1)).findAll(any());
    }

    @Test
    @DisplayName("GET /api/v1/users/count - Should return count of users")
    void testGetAllCountUserWithRolesAndPermissions() throws Exception {
        when(userService.getAllCountUserWithRolesAndPermissions()).thenReturn(15L);

        mockMvc.perform(get("/api/v1/users/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(15L));

        verify(userService, times(1)).getAllCountUserWithRolesAndPermissions();
    }

    @Test
    @DisplayName("GET /api/v1/users/findAll - Should return paged users with roles")
    void testGetAllUserWithRolesNotPermissions() throws Exception {
        Page<AppUser> page = new PageImpl<>(List.of(sampleUser));
        when(userService.getAllUserWithRolesNotPermissions(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/users/findAll?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        verify(userService, times(1)).getAllUserWithRolesNotPermissions(any());
    }

    @Test
    @DisplayName("GET /api/v1/users/roles - Should return users with roles")
    void testGetAllUserWithRoles() throws Exception {
        Page<AppUser> page = new PageImpl<>(List.of(sampleUser));
        when(userService.getAllUserWithRoles(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/users/roles?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        verify(userService, times(1)).getAllUserWithRoles(any());
    }

    @Test
    @DisplayName("GET /api/v1/users/{userId}/roles - Should return user's roles")
    void testGetAppUserRoles() throws Exception {
        sampleUser.setRoles(Set.of(sampleRole));
        when(userService.getAppUserWithRoles(1L)).thenReturn(sampleUser);

        mockMvc.perform(get("/api/v1/users/{userId}/roles", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].roleName").value("ROLE_USER"));

        verify(userService, times(1)).getAppUserWithRoles(1L);
    }

    @Test
    @DisplayName("GET /api/v1/users/roles/permissions - Should return users with roles and permissions")
    void testGetAllUserWithRolesAndPermissions() throws Exception {
        Page<AppUser> page = new PageImpl<>(List.of(sampleUser));
        when(userService.getAllUserWithRolesAndPermissions(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/users/roles/permissions?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        verify(userService, times(1)).getAllUserWithRolesAndPermissions(any());
    }

    @Test
    @DisplayName("PUT /api/v1/users/{userId}/roles - Should assign roles to user")
    void testAssignRolesToUser() throws Exception {
        sampleUser.setRoles(Set.of(sampleRole));
        when(userService.assignRolesToUser(eq(1L), any())).thenReturn(sampleUser);

        mockMvc.perform(put("/api/v1/users/{userId}/roles", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Set.of(sampleRole))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L));

        verify(userService, times(1)).assignRolesToUser(eq(1L), any());
    }

    @Test
    @DisplayName("DELETE /api/v1/users/{userId}/roles - Should remove roles from user")
    void testRemoveRolesFromUser() throws Exception {
        when(userService.removeRolesFromUser(eq(1L), any())).thenReturn(sampleUser);

        mockMvc.perform(delete("/api/v1/users/{userId}/roles", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Set.of(sampleRole))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L));

        verify(userService, times(1)).removeRolesFromUser(eq(1L), any());
    }

    @Test
    @DisplayName("GET /api/v1/users/with-groups - Should return users with groups")
    void testGetAllUsersWithGroups() throws Exception {
        sampleUser.setUserGroups(Set.of(sampleGroup));
        when(userService.getAllUsersWithGroups()).thenReturn(List.of(sampleUser));

        mockMvc.perform(get("/api/v1/users/with-groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email").value("john@example.com"));

        verify(userService, times(1)).getAllUsersWithGroups();
    }

    @Test
    @DisplayName("GET /api/v1/users/{userId}/with-groups - Should return user with groups when found")
    void testGetUserWithGroupsById_Success() throws Exception {
        sampleUser.setUserGroups(Set.of(sampleGroup));
        when(userService.getUserWithGroupsById(1L)).thenReturn(Optional.of(sampleUser));

        mockMvc.perform(get("/api/v1/users/{userId}/with-groups", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L));

        verify(userService, times(1)).getUserWithGroupsById(1L);
    }

    @Test
    @DisplayName("GET /api/v1/users/{userId}/with-groups - Should return 404 when not found")
    void testGetUserWithGroupsById_NotFound() throws Exception {
        when(userService.getUserWithGroupsById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/users/{userId}/with-groups", 99L))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserWithGroupsById(99L);
    }

    @Test
    @DisplayName("GET /api/v1/users/{userId}/groups - Should return user groups")
    void testGetUserGroups() throws Exception {
        when(userService.getUserGroups(1L)).thenReturn(Set.of(sampleGroup));

        mockMvc.perform(get("/api/v1/users/{userId}/groups", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].groupName").value("Developers"));

        verify(userService, times(1)).getUserGroups(1L);
    }

    @Test
    @DisplayName("POST /api/v1/users/{userId}/groups/{groupId} - Should add user to group")
    void testAddUserToGroup() throws Exception {
        doNothing().when(userService).addUserToGroup(1L, 100L);

        mockMvc.perform(post("/api/v1/users/{userId}/groups/{groupId}", 1L, 100L))
                .andExpect(status().isOk());

        verify(userService, times(1)).addUserToGroup(1L, 100L);
    }

    @Test
    @DisplayName("DELETE /api/v1/users/{userId}/groups/{groupId} - Should remove user from group")
    void testRemoveUserFromGroup() throws Exception {
        doNothing().when(userService).removeUserFromGroup(1L, 100L);

        mockMvc.perform(delete("/api/v1/users/{userId}/groups/{groupId}", 1L, 100L))
                .andExpect(status().isOk());

        verify(userService, times(1)).removeUserFromGroup(1L, 100L);
    }
}
