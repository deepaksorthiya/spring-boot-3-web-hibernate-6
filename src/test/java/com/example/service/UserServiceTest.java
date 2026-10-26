package com.example.service;

import com.example.entity.AppUser;
import com.example.entity.Role;
import com.example.entity.UserGroup;
import com.example.global.exceptions.ResourceNotFoundException;
import com.example.repository.RoleRepository;
import com.example.repository.UserGroupRepository;
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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserGroupRepository groupRepository;

    @InjectMocks
    private UserService userService;

    private AppUser sampleUser;
    private Role sampleRole;
    private UserGroup sampleGroup;

    @BeforeEach
    void setUp() {
        sampleUser = new AppUser("john@example.com", "Secret@123", "Secret@123", "John", "Doe");
        sampleUser.setUserId(1L);

        sampleRole = new Role("ROLE_USER", "Standard user role");
        sampleRole.setRoleId(10L);

        sampleGroup = new UserGroup("Developers", "Development team");
        sampleGroup.setUserGroupId(100L);
    }

    @Test
    @DisplayName("Should create user successfully")
    void testCreateUser() {
        when(userRepository.save(sampleUser)).thenReturn(sampleUser);

        AppUser created = userService.createUser(sampleUser);

        assertNotNull(created);
        assertEquals(sampleUser.getEmail(), created.getEmail());
        verify(userRepository, times(1)).save(sampleUser);
    }

    @Test
    @DisplayName("Should delete user by ID when user exists")
    void testDeleteAppUserById_Success() {
        Role role = new Role("ROLE_USER", "User role");
        role.setRoleId(10L);
        UserGroup group = new UserGroup("Admins", "Admin group");
        group.setUserGroupId(20L);

        sampleUser.addRole(role);
        sampleUser.addToGroup(group);

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        doNothing().when(userRepository).delete(sampleUser);

        userService.deleteAppUserById(1L);

        assertThat(sampleUser.getRoles()).isEmpty();
        assertThat(role.getAppUsers()).doesNotContain(sampleUser);
        assertThat(sampleUser.getUserGroups()).isEmpty();
        assertThat(group.getAppUsers()).doesNotContain(sampleUser);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).delete(sampleUser);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent user")
    void testDeleteAppUserById_NotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteAppUserById(99L));
        verify(userRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should return user by ID when user exists")
    void testGetAppUserById_Success() {
        when(userRepository.findByUserId(1L)).thenReturn(Optional.of(sampleUser));

        AppUser found = userService.getAppUserById(1L);

        assertNotNull(found);
        assertEquals(1L, found.getUserId());
        verify(userRepository, times(1)).findByUserId(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user by ID not found")
    void testGetAppUserById_NotFound() {
        when(userRepository.findByUserId(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getAppUserById(99L));
    }

    @Test
    @DisplayName("Should find all users with pagination")
    void testFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<AppUser> page = new PageImpl<>(List.of(sampleUser));
        when(userRepository.findAll(pageable)).thenReturn(page);

        Page<AppUser> result = userService.findAll(pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should return count of users with roles and permissions")
    void testGetAllCountUserWithRolesAndPermissions() {
        when(userRepository.getAllCountUserWithRolesAndPermissions()).thenReturn(42L);

        Long count = userService.getAllCountUserWithRolesAndPermissions();

        assertEquals(42L, count);
        verify(userRepository, times(1)).getAllCountUserWithRolesAndPermissions();
    }

    @Test
    @DisplayName("Should get all users with roles not permissions")
    void testGetAllUserWithRolesNotPermissions() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<AppUser> page = new PageImpl<>(List.of(sampleUser));
        when(userRepository.findAll(pageable)).thenReturn(page);

        Page<AppUser> result = userService.getAllUserWithRolesNotPermissions(pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should get all users with roles")
    void testGetAllUserWithRoles() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<AppUser> page = new PageImpl<>(List.of(sampleUser));
        when(userRepository.getAllUserWithRoles(pageable)).thenReturn(page);

        Page<AppUser> result = userService.getAllUserWithRoles(pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(userRepository, times(1)).getAllUserWithRoles(pageable);
    }

    @Test
    @DisplayName("Should get all users with roles and permissions")
    void testGetAllUserWithRolesAndPermissions() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<AppUser> page = new PageImpl<>(List.of(sampleUser));
        when(userRepository.getAllUserWithRolesAndPermissions(pageable)).thenReturn(page);

        Page<AppUser> result = userService.getAllUserWithRolesAndPermissions(pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(userRepository, times(1)).getAllUserWithRolesAndPermissions(pageable);
    }

    @Test
    @DisplayName("Should assign new roles to user")
    void testAssignRolesToUser_WithNewRoles() {
        sampleUser.setRoles(new HashSet<>());
        when(userRepository.getUserWithRoles(1L)).thenReturn(Optional.of(sampleUser));
        when(roleRepository.findByRoleIdIn(Set.of(10L))).thenReturn(Set.of(sampleRole));

        AppUser updated = userService.assignRolesToUser(1L, Set.of(sampleRole));

        assertThat(updated.getRoles()).contains(sampleRole);
        verify(roleRepository, times(1)).findByRoleIdIn(Set.of(10L));
    }

    @Test
    @DisplayName("Should skip assigning roles when all roles already exist")
    void testAssignRolesToUser_AlreadyAssigned() {
        sampleUser.setRoles(new HashSet<>(Set.of(sampleRole)));
        when(userRepository.getUserWithRoles(1L)).thenReturn(Optional.of(sampleUser));

        AppUser updated = userService.assignRolesToUser(1L, Set.of(sampleRole));

        assertThat(updated.getRoles()).contains(sampleRole);
        verify(roleRepository, never()).findByRoleIdIn(any());
    }

    @Test
    @DisplayName("Should return user immediately when assigning null or empty roles")
    void testAssignRolesToUser_NullOrEmptyRoles() {
        when(userRepository.getUserWithRoles(1L)).thenReturn(Optional.of(sampleUser));

        AppUser updatedNull = userService.assignRolesToUser(1L, null);
        AppUser updatedEmpty = userService.assignRolesToUser(1L, Set.of());

        assertThat(updatedNull).isSameAs(sampleUser);
        assertThat(updatedEmpty).isSameAs(sampleUser);
        verify(roleRepository, never()).findByRoleIdIn(any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when assigning roles to non-existent user")
    void testAssignRolesToUser_UserNotFound() {
        when(userRepository.getUserWithRoles(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.assignRolesToUser(99L, Set.of(sampleRole)));
    }

    @Test
    @DisplayName("Should remove roles from user")
    void testRemoveRolesFromUser_MatchingRoles() {
        sampleUser.setRoles(new HashSet<>(Set.of(sampleRole)));
        sampleRole.getAppUsers().add(sampleUser);
        when(userRepository.getUserWithRoles(1L)).thenReturn(Optional.of(sampleUser));

        AppUser updated = userService.removeRolesFromUser(1L, Set.of(sampleRole));

        assertThat(updated.getRoles()).doesNotContain(sampleRole);
        assertThat(sampleRole.getAppUsers()).doesNotContain(sampleUser);
        verify(roleRepository, never()).findByRoleIdIn(any());
    }

    @Test
    @DisplayName("Should not alter roles when removing non-matching roles")
    void testRemoveRolesFromUser_NonMatchingRoles() {
        sampleUser.setRoles(new HashSet<>());
        when(userRepository.getUserWithRoles(1L)).thenReturn(Optional.of(sampleUser));

        AppUser updated = userService.removeRolesFromUser(1L, Set.of(sampleRole));

        assertThat(updated.getRoles()).isEmpty();
        verify(roleRepository, never()).findByRoleIdIn(any());
    }

    @Test
    @DisplayName("Should return user immediately when removing null or empty roles")
    void testRemoveRolesFromUser_NullOrEmptyRoles() {
        when(userRepository.getUserWithRoles(1L)).thenReturn(Optional.of(sampleUser));

        AppUser updatedNull = userService.removeRolesFromUser(1L, null);
        AppUser updatedEmpty = userService.removeRolesFromUser(1L, Set.of());

        assertThat(updatedNull).isSameAs(sampleUser);
        assertThat(updatedEmpty).isSameAs(sampleUser);
        verify(roleRepository, never()).findByRoleIdIn(any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when removing roles from non-existent user")
    void testRemoveRolesFromUser_UserNotFound() {
        when(userRepository.getUserWithRoles(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.removeRolesFromUser(99L, Set.of(sampleRole)));
    }

    @Test
    @DisplayName("Should return user with roles when user exists")
    void testGetAppUserWithRoles_Success() {
        sampleUser.setRoles(new HashSet<>(Set.of(sampleRole)));
        when(userRepository.getUserWithRoles(1L)).thenReturn(Optional.of(sampleUser));

        AppUser result = userService.getAppUserWithRoles(1L);

        assertNotNull(result);
        assertThat(result.getRoles()).contains(sampleRole);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when getAppUserWithRoles not found")
    void testGetAppUserWithRoles_NotFound() {
        when(userRepository.getUserWithRoles(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getAppUserWithRoles(99L));
    }

    @Test
    @DisplayName("Should return all users with groups")
    void testGetAllUsersWithGroups() {
        when(userRepository.findAllWithGroups()).thenReturn(List.of(sampleUser));

        List<AppUser> result = userService.getAllUsersWithGroups();

        assertThat(result).hasSize(1);
        verify(userRepository, times(1)).findAllWithGroups();
    }

    @Test
    @DisplayName("Should return user groups when user exists")
    void testGetUserGroups_Success() {
        sampleUser.setUserGroups(new HashSet<>(Set.of(sampleGroup)));
        when(userRepository.findByUserIdWithGroups(1L)).thenReturn(Optional.of(sampleUser));

        Set<UserGroup> groups = userService.getUserGroups(1L);

        assertThat(groups).contains(sampleGroup);
    }

    @Test
    @DisplayName("Should throw RuntimeException when getUserGroups user not found")
    void testGetUserGroups_NotFound() {
        when(userRepository.findByUserIdWithGroups(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.getUserGroups(99L));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    @DisplayName("Should return optional user with groups by ID")
    void testGetUserWithGroupsById() {
        when(userRepository.findByUserIdWithGroups(1L)).thenReturn(Optional.of(sampleUser));

        Optional<AppUser> result = userService.getUserWithGroupsById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getUserId());
    }

    @Test
    @DisplayName("Should add user to group successfully")
    void testAddUserToGroup_Success() {
        sampleUser.setUserGroups(new HashSet<>());
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(groupRepository.findById(100L)).thenReturn(Optional.of(sampleGroup));
        when(groupRepository.save(sampleGroup)).thenReturn(sampleGroup);

        userService.addUserToGroup(1L, 100L);

        assertTrue(sampleUser.getUserGroups().contains(sampleGroup));
        assertTrue(sampleGroup.getAppUsers().contains(sampleUser));
        verify(groupRepository, times(1)).save(sampleGroup);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw RuntimeException when adding user to group and user not found")
    void testAddUserToGroup_UserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.addUserToGroup(99L, 100L));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw RuntimeException when adding user to group and group not found")
    void testAddUserToGroup_GroupNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(groupRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.addUserToGroup(1L, 999L));
        assertEquals("Group not found", ex.getMessage());
    }

    @Test
    @DisplayName("Should remove user from group successfully")
    void testRemoveUserFromGroup_Success() {
        sampleUser.setUserGroups(new HashSet<>(Set.of(sampleGroup)));
        sampleGroup.getAppUsers().add(sampleUser);

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(groupRepository.findById(100L)).thenReturn(Optional.of(sampleGroup));
        when(groupRepository.save(sampleGroup)).thenReturn(sampleGroup);

        userService.removeUserFromGroup(1L, 100L);

        assertFalse(sampleUser.getUserGroups().contains(sampleGroup));
        assertFalse(sampleGroup.getAppUsers().contains(sampleUser));
        verify(groupRepository, times(1)).save(sampleGroup);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw RuntimeException when removing user from group and user not found")
    void testRemoveUserFromGroup_UserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.removeUserFromGroup(99L, 100L));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw RuntimeException when removing user from group and group not found")
    void testRemoveUserFromGroup_GroupNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(groupRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.removeUserFromGroup(1L, 999L));
        assertEquals("Group not found", ex.getMessage());
    }
}
