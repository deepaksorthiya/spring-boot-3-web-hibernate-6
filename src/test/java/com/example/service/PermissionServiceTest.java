package com.example.service;

import com.example.entity.Permission;
import com.example.entity.Role;
import com.example.global.exceptions.ResourceNotFoundException;
import com.example.repository.PermissionRepository;
import com.example.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private PermissionService permissionService;

    private Permission samplePermission;

    @BeforeEach
    void setUp() {
        samplePermission = new Permission("READ", "Read permission");
        samplePermission.setPermissionId(1L);
    }

    @Test
    @DisplayName("Should create permission successfully")
    void testCreatePermission() {
        when(permissionRepository.save(samplePermission)).thenReturn(samplePermission);

        Permission created = permissionService.createPermission(samplePermission);

        assertNotNull(created);
        assertEquals("READ", created.getPermissionName());
        verify(permissionRepository, times(1)).save(samplePermission);
    }

    @Test
    @DisplayName("Should delete permission by ID")
    void testDeleteByPermissionId() {
        Role role = new Role("ROLE_ADMIN", "Admin");
        samplePermission.getRoles().add(role);
        role.getPermissions().add(samplePermission);

        when(permissionRepository.findById(1L)).thenReturn(Optional.of(samplePermission));
        doNothing().when(permissionRepository).delete(samplePermission);

        permissionService.deleteByPermissionId(1L);

        verify(permissionRepository, times(1)).findById(1L);
        verify(permissionRepository, times(1)).delete(samplePermission);
        assertThat(role.getPermissions()).doesNotContain(samplePermission);
    }

    @Test
    @DisplayName("Should find permission by ID when permission exists")
    void testFindByPermissionId_Success() {
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(samplePermission));

        Permission found = permissionService.findByPermissionId(1L);

        assertNotNull(found);
        assertEquals(1L, found.getPermissionId());
        verify(permissionRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when permission by ID not found")
    void testFindByPermissionId_NotFound() {
        when(permissionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> permissionService.findByPermissionId(99L));
    }

    @Test
    @DisplayName("Should find all permissions with pagination")
    void testFindAllPermissions() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Permission> page = new PageImpl<>(List.of(samplePermission));
        when(permissionRepository.findAll(pageable)).thenReturn(page);

        Page<Permission> result = permissionService.findAllPermissions(pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(permissionRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should get all roles by permission ID")
    void testGetAllRolesByPermissionId() {
        Pageable pageable = PageRequest.of(0, 10);
        Role role = new Role("ROLE_ADMIN", "Admin");
        Page<Role> page = new PageImpl<>(List.of(role));
        when(roleRepository.findByPermissionsPermissionId(1L, pageable)).thenReturn(page);

        Page<Role> result = permissionService.getAllRolesByPermissionId(1L, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(roleRepository, times(1)).findByPermissionsPermissionId(1L, pageable);
    }
}
