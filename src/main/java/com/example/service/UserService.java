package com.example.service;

import com.example.entity.AppUser;
import com.example.entity.Role;
import com.example.entity.UserGroup;
import com.example.global.exceptions.ResourceNotFoundException;
import com.example.repository.RoleRepository;
import com.example.repository.UserGroupRepository;
import com.example.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserGroupRepository groupRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, UserGroupRepository groupRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.groupRepository = groupRepository;
    }

    public AppUser createUser(AppUser user) {
        return userRepository.save(user);
    }

    @Transactional
    public void deleteAppUserById(Long userId) {
        AppUser user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(userId));
        // Remove the user from roles to update the join table
        user.setRoles(null);
        userRepository.delete(user);
    }

    public AppUser getAppUserById(Long userId) {
        return userRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException(userId));
    }

    public Page<AppUser> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Long getAllCountUserWithRolesAndPermissions() {
        return userRepository.getAllCountUserWithRolesAndPermissions();
    }

    public Page<AppUser> getAllUserWithRolesNotPermissions(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Page<AppUser> getAllUserWithRoles(Pageable pageable) {
        return userRepository.getAllUserWithRoles(pageable);
    }

    public Page<AppUser> getAllUserWithRolesAndPermissions(Pageable pageable) {
        return userRepository.getAllUserWithRolesAndPermissions(pageable);
    }

    @Transactional
    public AppUser assignRolesToUser(Long userId, Set<Role> roles) {
        AppUser appUser = userRepository.getUserWithRoles(userId)
                .orElseThrow(() -> new ResourceNotFoundException(userId));
        Set<Role> appUserRoles = appUser.getRoles();
        Set<Role> unassignRoles = roles.stream().filter(role -> !appUserRoles.contains(role)).collect(Collectors.toSet());
        if (!unassignRoles.isEmpty()) {
            Set<Long> unassignRoleIds = unassignRoles.stream().map(Role::getRoleId).collect(Collectors.toSet());
            Set<Role> rolesToAssign = roleRepository.findByRoleIdIn(unassignRoleIds);
            appUser.getRoles().addAll(rolesToAssign);
        }
        return appUser;
    }

    @Transactional
    public AppUser removeRolesFromUser(Long userId, Set<Role> roles) {
        AppUser appUser = userRepository.getUserWithRoles(userId)
                .orElseThrow(() -> new ResourceNotFoundException(userId));
        Set<Long> filterDelRoles = appUser.getRoles().stream().filter(roles::contains).map(Role::getRoleId).collect(Collectors.toSet());
        if (!filterDelRoles.isEmpty()) {
            Set<Role> deletedRoles = roleRepository.findByRoleIdIn(filterDelRoles);
            appUser.getRoles().removeAll(deletedRoles);
        }
        return appUser;
    }

    public AppUser getAppUserWithRoles(Long userId) {
        return userRepository.getUserWithRoles(userId)
                .orElseThrow(() -> new ResourceNotFoundException(userId));
    }

    @Transactional(readOnly = true)
    public List<AppUser> getAllUsersWithGroups() {
        return userRepository.findAllWithGroups();
    }

    @Transactional(readOnly = true)
    public Set<UserGroup> getUserGroups(Long userId) {
        AppUser appUser = userRepository.findByUserIdWithGroups(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return appUser.getUserGroups();
    }

    @Transactional(readOnly = true)
    public Optional<AppUser> getUserWithGroupsById(Long id) {
        return userRepository.findByUserIdWithGroups(id);
    }

    @Transactional
    public void addUserToGroup(Long userId, Long groupId) {
        AppUser appUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserGroup userGroup = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        appUser.addToGroup(userGroup);
        userRepository.save(appUser);
    }

    @Transactional
    public void removeUserFromGroup(Long userId, Long groupId) {
        AppUser appUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserGroup userGroup = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        appUser.removeFromGroup(userGroup);
        userRepository.save(appUser);
    }
}
