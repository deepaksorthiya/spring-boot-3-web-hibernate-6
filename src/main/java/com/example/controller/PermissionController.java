package com.example.controller;

import com.example.entity.Permission;
import com.example.entity.Role;
import com.example.service.PermissionService;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.v1.permissions.prefix}")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Permission> createPermission(@RequestBody @Validated Permission permission) {
        return new ResponseEntity<>(permissionService.createPermission(permission), HttpStatus.CREATED);
    }

    @DeleteMapping(value = "{permissionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteByPermissionId(@PathVariable Long permissionId) {
        permissionService.deleteByPermissionId(permissionId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "{permissionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Permission> findByPermissionId(@PathVariable Long permissionId) {
        return new ResponseEntity<>(permissionService.findByPermissionId(permissionId), HttpStatus.OK);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Permission>> findAllPermissions(@NotNull final Pageable pageable) {
        return new ResponseEntity<>(permissionService.findAllPermissions(pageable), HttpStatus.OK);
    }

    @GetMapping(value = "{permissionId}/roles", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Role>> getAllRolesByPermissionId(@PathVariable Long permissionId, @NotNull final Pageable pageable) {
        return new ResponseEntity<>(permissionService.getAllRolesByPermissionId(permissionId, pageable), HttpStatus.OK);
    }

}
