package com.example.controller;

import com.example.config.WebAppConfig;
import com.example.entity.AppUser;
import com.example.entity.Role;
import com.example.global.exceptions.ResourceNotFoundException;
import com.example.global.exceptions.UserControllerAdviceHandler;
import com.example.service.RoleService;
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

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoleController.class)
@Import({WebAppConfig.class, UserControllerAdviceHandler.class})
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RoleService roleService;

    private Role sampleRole;

    @BeforeEach
    void setUp() {
        sampleRole = new Role("ROLE_ADMIN", "Administrator Role");
        sampleRole.setRoleId(1L);
    }

    @Test
    @DisplayName("POST /api/v1/roles - Should create role")
    void testCreateRole() throws Exception {
        when(roleService.createRole(any(Role.class))).thenReturn(sampleRole);

        mockMvc.perform(post("/api/v1/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRole)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roleId").value(1L))
                .andExpect(jsonPath("$.roleName").value("ROLE_ADMIN"));

        verify(roleService, times(1)).createRole(any(Role.class));
    }

    @Test
    @DisplayName("DELETE /api/v1/roles/{roleId} - Should delete role by ID")
    void testDeleteRoleById() throws Exception {
        doNothing().when(roleService).deleteRoleById(1L);

        mockMvc.perform(delete("/api/v1/roles/{roleId}", 1L))
                .andExpect(status().isOk());

        verify(roleService, times(1)).deleteRoleById(1L);
    }

    @Test
    @DisplayName("GET /api/v1/roles/{roleId} - Should return role when found")
    void testFindByRoleId_Success() throws Exception {
        when(roleService.findByRoleId(1L)).thenReturn(sampleRole);

        mockMvc.perform(get("/api/v1/roles/{roleId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roleId").value(1L))
                .andExpect(jsonPath("$.roleName").value("ROLE_ADMIN"));

        verify(roleService, times(1)).findByRoleId(1L);
    }

    @Test
    @DisplayName("GET /api/v1/roles/{roleId} - Should return 404 when role not found")
    void testFindByRoleId_NotFound() throws Exception {
        when(roleService.findByRoleId(99L)).thenThrow(new ResourceNotFoundException(99L));

        mockMvc.perform(get("/api/v1/roles/{roleId}", 99L))
                .andExpect(status().isNotFound());

        verify(roleService, times(1)).findByRoleId(99L);
    }

    @Test
    @DisplayName("GET /api/v1/roles - Should return paged roles with permissions")
    void testGetRoleWithPermissions() throws Exception {
        Page<Role> page = new PageImpl<>(List.of(sampleRole), PageRequest.of(0, 10), 1);
        when(roleService.getRoleWithPermissions(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/roles?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].roleName").value("ROLE_ADMIN"));

        verify(roleService, times(1)).getRoleWithPermissions(any());
    }

    @Test
    @DisplayName("GET /api/v1/roles/{roleId}/users - Should return users assigned to role")
    void testFindAllAppUsersByRoleId() throws Exception {
        AppUser user = new AppUser("user@test.com", "Pass@1234", "Pass@1234", "A", "B");
        user.setUserId(2L);
        Page<AppUser> page = new PageImpl<>(List.of(user), PageRequest.of(0, 10), 1);
        when(roleService.findAllAppUsersByRoleId(eq(1L), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/roles/{roleId}/users?page=0&size=10", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].userId").value(2L));

        verify(roleService, times(1)).findAllAppUsersByRoleId(eq(1L), any());
    }
}
