package com.example.controller;

import com.example.config.WebAppConfig;
import com.example.entity.Permission;
import com.example.entity.Role;
import com.example.global.exceptions.ResourceNotFoundException;
import com.example.global.exceptions.UserControllerAdviceHandler;
import com.example.service.PermissionService;
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

@WebMvcTest(PermissionController.class)
@Import({WebAppConfig.class, UserControllerAdviceHandler.class})
class PermissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PermissionService permissionService;

    private Permission samplePermission;

    @BeforeEach
    void setUp() {
        samplePermission = new Permission("READ", "Read access");
        samplePermission.setPermissionId(1L);
    }

    @Test
    @DisplayName("POST /api/v1/permissions - Should create permission")
    void testCreatePermission() throws Exception {
        when(permissionService.createPermission(any(Permission.class))).thenReturn(samplePermission);

        mockMvc.perform(post("/api/v1/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(samplePermission)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.permissionId").value(1L))
                .andExpect(jsonPath("$.permissionName").value("READ"));

        verify(permissionService, times(1)).createPermission(any(Permission.class));
    }

    @Test
    @DisplayName("DELETE /api/v1/permissions/{permissionId} - Should delete permission by ID")
    void testDeleteByPermissionId() throws Exception {
        doNothing().when(permissionService).deleteByPermissionId(1L);

        mockMvc.perform(delete("/api/v1/permissions/{permissionId}", 1L))
                .andExpect(status().isOk());

        verify(permissionService, times(1)).deleteByPermissionId(1L);
    }

    @Test
    @DisplayName("GET /api/v1/permissions/{permissionId} - Should return permission when found")
    void testFindByPermissionId_Success() throws Exception {
        when(permissionService.findByPermissionId(1L)).thenReturn(samplePermission);

        mockMvc.perform(get("/api/v1/permissions/{permissionId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.permissionId").value(1L))
                .andExpect(jsonPath("$.permissionName").value("READ"));

        verify(permissionService, times(1)).findByPermissionId(1L);
    }

    @Test
    @DisplayName("GET /api/v1/permissions/{permissionId} - Should return 404 when permission not found")
    void testFindByPermissionId_NotFound() throws Exception {
        when(permissionService.findByPermissionId(99L)).thenThrow(new ResourceNotFoundException(99L));

        mockMvc.perform(get("/api/v1/permissions/{permissionId}", 99L))
                .andExpect(status().isNotFound());

        verify(permissionService, times(1)).findByPermissionId(99L);
    }

    @Test
    @DisplayName("GET /api/v1/permissions - Should return paged permissions")
    void testFindAllPermissions() throws Exception {
        Page<Permission> page = new PageImpl<>(List.of(samplePermission), PageRequest.of(0, 10), 1);
        when(permissionService.findAllPermissions(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/permissions?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].permissionName").value("READ"));

        verify(permissionService, times(1)).findAllPermissions(any());
    }

    @Test
    @DisplayName("GET /api/v1/permissions/{permissionId}/roles - Should return roles having permission")
    void testGetAllRolesByPermissionId() throws Exception {
        Role role = new Role("ROLE_ADMIN", "Admin");
        role.setRoleId(5L);
        Page<Role> page = new PageImpl<>(List.of(role), PageRequest.of(0, 10), 1);
        when(permissionService.getAllRolesByPermissionId(eq(1L), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/permissions/{permissionId}/roles?page=0&size=10", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].roleId").value(5L));

        verify(permissionService, times(1)).getAllRolesByPermissionId(eq(1L), any());
    }
}
