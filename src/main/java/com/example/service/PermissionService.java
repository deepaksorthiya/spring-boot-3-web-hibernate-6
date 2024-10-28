package com.example.service;

import com.example.entity.Permission;
import com.example.global.exceptions.ResourceNotFoundException;
import com.example.repository.PermissionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public Permission createPermission(Permission permission) {
        return permissionRepository.save(permission);
    }

    @Transactional
    public void deleteByPermissionId(@PathVariable Long permissionId) {
        permissionRepository.deleteById(permissionId);
    }

    public Permission findByPermissionId(@PathVariable Long permissionId) {
        return permissionRepository.findById(permissionId).orElseThrow(() -> new ResourceNotFoundException(permissionId));
    }

    public Page<Permission> findAllPermissions(Pageable pageable) {
        return permissionRepository.findAll(pageable);
    }

}
