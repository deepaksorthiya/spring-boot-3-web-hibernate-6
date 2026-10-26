package com.example.service;

import com.example.entity.AppUser;
import com.example.entity.Role;
import com.example.global.exceptions.ResourceNotFoundException;
import com.example.repository.RoleRepository;
import com.example.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public RoleService(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    @Transactional
    public void deleteRoleById(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException(roleId));

        // 1. Fetch all users who have this role WITH their roles in 1 single JPQL query (avoids N+1 lazy SELECTs)
        List<AppUser> users = userRepository.findAllWithRolesByRoleId(roleId);
        for (AppUser user : users) {
            user.getRoles().remove(role);
        }

        // 2. Clear permissions on the owning side (Role owns permissions)
        role.getPermissions().clear();

        // 3. Delete the role
        roleRepository.delete(role);
    }

    public Role findByRoleId(Long roleId) {
        return roleRepository.findByRoleId(roleId).orElseThrow(() -> new ResourceNotFoundException(roleId));
    }

    public Page<Role> getRoleWithPermissions(Pageable pageable) {
        return roleRepository.getRoleWithPermissions(pageable);
    }

    public Page<AppUser> findAllAppUsersByRoleId(Long roleId, Pageable pageable) {
        return userRepository.findByRolesRoleId(roleId, pageable);
    }
}
