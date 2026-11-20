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

import java.util.*;
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
        // Disassociate from user groups (UserGroup is the owning side of users_group_app_users_mapping)
        Set<UserGroup> groups = new HashSet<>(user.getUserGroups());
        for (UserGroup group : groups) {
            user.removeFromGroup(group);
        }
        // Disassociate from roles (AppUser is the owning side of app_users_roles_mapping)
        Set<Role> roles = new HashSet<>(user.getRoles());
        for (Role role : roles) {
            user.removeRole(role);
        }
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

        // Early return if no roles are provided in the request
        if (roles == null || roles.isEmpty()) {
            return appUser;
        }

        // Collect existing role IDs already assigned to the user to avoid duplicate DB queries
        Set<Long> existingRoleIds = appUser.getRoles().stream()
                .map(Role::getRoleId)
                .collect(Collectors.toSet());

        // Extract IDs for newly requested roles that the user doesn't already have
        Set<Long> newRoleIds = roles.stream()
                .map(Role::getRoleId)
                .filter(id -> id != null && !existingRoleIds.contains(id))
                .collect(Collectors.toSet());

        if (!newRoleIds.isEmpty()) {
            // Incoming Role objects from the request payload are detached/unmanaged POJOs.
            // Querying findByRoleIdIn validates that the roles exist in the database and loads
            // fully managed Role entity instances into the current Hibernate session
            // (one IN query; the response needs the role data, so proxies via getReferenceById
            // would only defer the load to serialization, outside the transaction).
            Set<Role> rolesToAssign = roleRepository.findByRoleIdIn(newRoleIds);

            // PERFORMANCE: update the OWNING side only (AppUser.roles defines the @JoinTable).
            // Do NOT use appUser.addRole(role) here: it also calls role.getAppUsers().add(user), and
            // Role.appUsers is a lazy inverse (mappedBy) collection, so that add() forces Hibernate to
            // initialize it, loading EVERY user holding that role, once per role assigned, just to
            // update an in-memory view that is never persisted and never read in this transaction.
            // Hibernate writes the join-table rows (batched) from the owning side alone.
            appUser.getRoles().addAll(rolesToAssign);
        }
        return appUser;
    }

    @Transactional
    public AppUser removeRolesFromUser(Long userId, Set<Role> roles) {
        AppUser appUser = userRepository.getUserWithRoles(userId)
                .orElseThrow(() -> new ResourceNotFoundException(userId));

        // Early return if no roles to remove are specified
        if (roles == null || roles.isEmpty()) {
            return appUser;
        }

        // Extract the role IDs requested for removal from the incoming unmanaged roles
        Set<Long> roleIdsToRemove = roles.stream()
                .map(Role::getRoleId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (!roleIdsToRemove.isEmpty()) {
            // Note: No DB query (roleRepository.findByRoleIdIn) is needed here!
            // All assigned roles are already loaded and managed in memory on appUser.getRoles()
            // by getUserWithRoles(userId), so we filter those in-memory managed instances.

            // PERFORMANCE: update the OWNING side only (AppUser.roles defines the @JoinTable).
            // Do NOT use appUser.removeRole(role) here: it also calls role.getAppUsers().remove(user),
            // and Role.appUsers is a lazy inverse (mappedBy) collection, so that remove() forces Hibernate
            // to initialize it, loading EVERY user holding that role, once per role removed, just to
            // update an in-memory view that is never persisted and never read in this transaction.
            // Hibernate deletes the matching app_users_roles_mapping rows from the owning side alone.
            appUser.getRoles().removeIf(role -> roleIdsToRemove.contains(role.getRoleId()));
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

        // UserGroup is the OWNING side of users_group_app_users_mapping (defines @JoinTable).
        // AppUser is the inverse side (mappedBy = "appUsers").
        // To persist the relationship in the join table, we must update the owning side and save UserGroup.
        userGroup.addAppUser(appUser);
        groupRepository.save(userGroup);
    }

    @Transactional
    public void removeUserFromGroup(Long userId, Long groupId) {
        AppUser appUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserGroup userGroup = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // UserGroup is the OWNING side of users_group_app_users_mapping (defines @JoinTable).
        // Removing the user from userGroup and saving userGroup ensures the join table row is deleted.
        userGroup.removeAppUser(appUser);
        groupRepository.save(userGroup);
    }
}
