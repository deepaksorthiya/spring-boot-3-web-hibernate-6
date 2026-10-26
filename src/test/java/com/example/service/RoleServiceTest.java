package com.example.service;

import com.example.entity.AppUser;
import com.example.entity.Permission;
import com.example.entity.Role;
import com.example.global.exceptions.ResourceNotFoundException;
import com.example.repository.RoleRepository;
import com.example.repository.UserRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RoleService roleService;

    private Role sampleRole;

    @BeforeEach
    void setUp() {
        sampleRole = new Role("ROLE_ADMIN", "Administrator Role");
        sampleRole.setRoleId(1L);
    }

    @Test
    @DisplayName("Should create role successfully")
    void testCreateRole() {
        when(roleRepository.save(sampleRole)).thenReturn(sampleRole);

        Role created = roleService.createRole(sampleRole);

        assertNotNull(created);
        assertEquals("ROLE_ADMIN", created.getRoleName());
        verify(roleRepository, times(1)).save(sampleRole);
    }

    @Test
    @DisplayName("Should delete role by ID when role exists")
    void testDeleteRoleById_Success() {
        AppUser user = new AppUser("user@example.com", "pass", "pass", "John", "Doe");
        user.setUserId(10L);
        user.getRoles().add(sampleRole);

        Permission permission = new Permission("READ", "Read perm");
        permission.setPermissionId(20L);
        sampleRole.getPermissions().add(permission);

        when(roleRepository.findById(1L)).thenReturn(Optional.of(sampleRole));
        when(userRepository.findAllWithRolesByRoleId(1L)).thenReturn(List.of(user));
        doNothing().when(roleRepository).delete(sampleRole);

        roleService.deleteRoleById(1L);

        assertThat(user.getRoles()).doesNotContain(sampleRole);
        assertThat(sampleRole.getPermissions()).isEmpty();
        verify(roleRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findAllWithRolesByRoleId(1L);
        verify(roleRepository, times(1)).delete(sampleRole);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent role")
    void testDeleteRoleById_NotFound() {
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roleService.deleteRoleById(99L));
        verify(userRepository, never()).findAllWithRolesByRoleId(any());
        verify(roleRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should find role by ID when role exists")
    void testFindByRoleId_Success() {
        when(roleRepository.findByRoleId(1L)).thenReturn(Optional.of(sampleRole));

        Role found = roleService.findByRoleId(1L);

        assertNotNull(found);
        assertEquals(1L, found.getRoleId());
        verify(roleRepository, times(1)).findByRoleId(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when role by ID not found")
    void testFindByRoleId_NotFound() {
        when(roleRepository.findByRoleId(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roleService.findByRoleId(99L));
    }

    @Test
    @DisplayName("Should get roles with permissions")
    void testGetRoleWithPermissions() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Role> page = new PageImpl<>(List.of(sampleRole));
        when(roleRepository.getRoleWithPermissions(pageable)).thenReturn(page);

        Page<Role> result = roleService.getRoleWithPermissions(pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(roleRepository, times(1)).getRoleWithPermissions(pageable);
    }

    @Test
    @DisplayName("Should find all users by role ID")
    void testFindAllAppUsersByRoleId() {
        Pageable pageable = PageRequest.of(0, 10);
        AppUser user = new AppUser("u@test.com", "Pass@1234", "Pass@1234", "A", "B");
        Page<AppUser> page = new PageImpl<>(List.of(user));
        when(userRepository.findByRolesRoleId(1L, pageable)).thenReturn(page);

        Page<AppUser> result = roleService.findAllAppUsersByRoleId(1L, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(userRepository, times(1)).findByRolesRoleId(1L, pageable);
    }
}
