package com.example.service;

import com.example.entity.AppUser;
import com.example.entity.UserGroup;
import com.example.repository.UserGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserGroupServiceTest {

    @Mock
    private UserGroupRepository groupRepository;

    @InjectMocks
    private UserGroupService groupService;

    private UserGroup sampleGroup;

    @BeforeEach
    void setUp() {
        sampleGroup = new UserGroup("Admins", "Administrator Group");
        sampleGroup.setUserGroupId(1L);
    }

    @Test
    @DisplayName("Should get all groups")
    void testGetAllGroups() {
        when(groupRepository.findAll()).thenReturn(List.of(sampleGroup));

        List<UserGroup> groups = groupService.getAllGroups();

        assertThat(groups).hasSize(1);
        verify(groupRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get all groups with users")
    void testGetAllGroupsWithUsers() {
        when(groupRepository.findAllWithUsers()).thenReturn(List.of(sampleGroup));

        List<UserGroup> groups = groupService.getAllGroupsWithUsers();

        assertThat(groups).hasSize(1);
        verify(groupRepository, times(1)).findAllWithUsers();
    }

    @Test
    @DisplayName("Should get group by ID")
    void testGetGroupById() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(sampleGroup));

        Optional<UserGroup> group = groupService.getGroupById(1L);

        assertTrue(group.isPresent());
        assertEquals("Admins", group.get().getGroupName());
        verify(groupRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should get group with users by ID")
    void testGetGroupWithUsersById() {
        when(groupRepository.findByIdWithUsers(1L)).thenReturn(Optional.of(sampleGroup));

        Optional<UserGroup> group = groupService.getGroupWithUsersById(1L);

        assertTrue(group.isPresent());
        assertEquals(1L, group.get().getUserGroupId());
        verify(groupRepository, times(1)).findByIdWithUsers(1L);
    }

    @Test
    @DisplayName("Should save group")
    void testSaveGroup() {
        when(groupRepository.save(sampleGroup)).thenReturn(sampleGroup);

        UserGroup saved = groupService.saveGroup(sampleGroup);

        assertNotNull(saved);
        assertEquals("Admins", saved.getGroupName());
        verify(groupRepository, times(1)).save(sampleGroup);
    }

    @Test
    @DisplayName("Should delete group by ID")
    void testDeleteGroup() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(sampleGroup));

        groupService.deleteGroup(1L);

        verify(groupRepository, times(1)).delete(sampleGroup);
    }

    @Test
    @DisplayName("Should return group users when group exists")
    void testGetGroupUsers_Success() {
        AppUser user = new AppUser("test@test.com", "Pass@1234", "Pass@1234", "A", "B");
        sampleGroup.getAppUsers().add(user);
        when(groupRepository.findByIdWithUsers(1L)).thenReturn(Optional.of(sampleGroup));

        Set<AppUser> users = groupService.getGroupUsers(1L);

        assertThat(users).contains(user);
    }

    @Test
    @DisplayName("Should throw RuntimeException when group users not found")
    void testGetGroupUsers_NotFound() {
        when(groupRepository.findByIdWithUsers(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> groupService.getGroupUsers(99L));
        assertEquals("Group not found", ex.getMessage());
    }
}
