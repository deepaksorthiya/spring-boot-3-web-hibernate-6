package com.example.service;

import com.example.entity.Role;
import com.example.global.exceptions.ResourceNotFoundException;
import com.example.repository.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    @Transactional
    public void deleteRoleById(Long roleId) {
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new ResourceNotFoundException(roleId));
        // Remove the user from roles to update the join table
        role.setPermissions(null);
        roleRepository.delete(role);
    }

    public Role findByRoleId(Long roleId) {
        return roleRepository.findByRoleId(roleId).orElseThrow(() -> new ResourceNotFoundException(roleId));
    }

    public Page<Role> getRoleWithPermissions(Pageable pageable) {
        return roleRepository.getRoleWithPermissions(pageable);
    }
}
