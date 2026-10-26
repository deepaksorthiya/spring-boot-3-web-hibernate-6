package com.example.controller;

import com.example.config.WebAppConfig;
import com.example.entity.AppUser;
import com.example.entity.UserGroup;
import com.example.global.exceptions.ResourceNotFoundException;
import com.example.global.exceptions.UserControllerAdviceHandler;
import com.example.service.UserGroupService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserGroupController.class)
@Import({WebAppConfig.class, UserControllerAdviceHandler.class})
class UserGroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserGroupService groupService;

    private UserGroup sampleGroup;

    @BeforeEach
    void setUp() {
        sampleGroup = new UserGroup("Developers", "Dev Team");
        sampleGroup.setUserGroupId(1L);
    }

    @Test
    @DisplayName("GET /api/v1/groups - Should return all groups")
    void testGetAllGroups() throws Exception {
        when(groupService.getAllGroups()).thenReturn(List.of(sampleGroup));

        mockMvc.perform(get("/api/v1/groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].groupName").value("Developers"));

        verify(groupService, times(1)).getAllGroups();
    }

    @Test
    @DisplayName("GET /api/v1/groups/with-users - Should return groups with users")
    void testGetAllGroupsWithUsers() throws Exception {
        when(groupService.getAllGroupsWithUsers()).thenReturn(List.of(sampleGroup));

        mockMvc.perform(get("/api/v1/groups/with-users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].groupName").value("Developers"));

        verify(groupService, times(1)).getAllGroupsWithUsers();
    }

    @Test
    @DisplayName("GET /api/v1/groups/{id} - Should return group when found")
    void testGetGroupById_Success() throws Exception {
        when(groupService.getGroupById(1L)).thenReturn(Optional.of(sampleGroup));

        mockMvc.perform(get("/api/v1/groups/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userGroupId").value(1L))
                .andExpect(jsonPath("$.groupName").value("Developers"));

        verify(groupService, times(1)).getGroupById(1L);
    }

    @Test
    @DisplayName("GET /api/v1/groups/{id} - Should return 404 when group not found")
    void testGetGroupById_NotFound() throws Exception {
        when(groupService.getGroupById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/groups/{id}", 99L))
                .andExpect(status().isNotFound());

        verify(groupService, times(1)).getGroupById(99L);
    }

    @Test
    @DisplayName("GET /api/v1/groups/{id}/with-users - Should return group with users when found")
    void testGetGroupWithUsersById_Success() throws Exception {
        when(groupService.getGroupWithUsersById(1L)).thenReturn(Optional.of(sampleGroup));

        mockMvc.perform(get("/api/v1/groups/{id}/with-users", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userGroupId").value(1L));

        verify(groupService, times(1)).getGroupWithUsersById(1L);
    }

    @Test
    @DisplayName("GET /api/v1/groups/{id}/with-users - Should return 404 when group not found")
    void testGetGroupWithUsersById_NotFound() throws Exception {
        when(groupService.getGroupWithUsersById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/groups/{id}/with-users", 99L))
                .andExpect(status().isNotFound());

        verify(groupService, times(1)).getGroupWithUsersById(99L);
    }

    @Test
    @DisplayName("GET /api/v1/groups/{id}/users - Should return group users")
    void testGetGroupUsers() throws Exception {
        AppUser user = new AppUser("user@test.com", "Pass@1234", "Pass@1234", "A", "B");
        user.setUserId(2L);
        when(groupService.getGroupUsers(1L)).thenReturn(Set.of(user));

        mockMvc.perform(get("/api/v1/groups/{id}/users", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userId").value(2L));

        verify(groupService, times(1)).getGroupUsers(1L);
    }

    @Test
    @DisplayName("POST /api/v1/groups - Should create group and return 201 Created")
    void testCreateGroup() throws Exception {
        when(groupService.saveGroup(any(UserGroup.class))).thenReturn(sampleGroup);

        mockMvc.perform(post("/api/v1/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleGroup)))
                .andExpect(status().isCreated());

        verify(groupService, times(1)).saveGroup(any(UserGroup.class));
    }

    @Test
    @DisplayName("PUT /api/v1/groups/{id} - Should update group when found")
    void testUpdateGroup_Success() throws Exception {
        when(groupService.getGroupById(1L)).thenReturn(Optional.of(sampleGroup));
        when(groupService.saveGroup(any(UserGroup.class))).thenReturn(sampleGroup);

        mockMvc.perform(put("/api/v1/groups/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleGroup)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupName").value("Developers"));

        verify(groupService, times(1)).getGroupById(1L);
        verify(groupService, times(1)).saveGroup(any(UserGroup.class));
    }

    @Test
    @DisplayName("PUT /api/v1/groups/{id} - Should return 404 when updating non-existent group")
    void testUpdateGroup_NotFound() throws Exception {
        when(groupService.getGroupById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/groups/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleGroup)))
                .andExpect(status().isNotFound());

        verify(groupService, times(1)).getGroupById(99L);
        verify(groupService, never()).saveGroup(any());
    }

    @Test
    @DisplayName("DELETE /api/v1/groups/{id} - Should delete group when found and return 204 No Content")
    void testDeleteGroup_Success() throws Exception {
        doNothing().when(groupService).deleteGroup(1L);

        mockMvc.perform(delete("/api/v1/groups/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(groupService, times(1)).deleteGroup(1L);
    }

    @Test
    @DisplayName("DELETE /api/v1/groups/{id} - Should return 404 when deleting non-existent group")
    void testDeleteGroup_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException(99L)).when(groupService).deleteGroup(99L);

        mockMvc.perform(delete("/api/v1/groups/{id}", 99L))
                .andExpect(status().isNotFound());

        verify(groupService, times(1)).deleteGroup(any());
    }
}
