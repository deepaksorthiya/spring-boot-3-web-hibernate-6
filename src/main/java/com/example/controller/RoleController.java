package com.example.controller;

import com.example.entity.AppUser;
import com.example.entity.Role;
import com.example.service.RoleService;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.v1.roles.prefix}")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Role> createRole(@RequestBody @Validated Role role) {
        return new ResponseEntity<>(roleService.createRole(role), HttpStatus.CREATED);
    }

    @DeleteMapping(value = "{roleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteRoleById(@PathVariable Long roleId) {
        roleService.deleteRoleById(roleId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "{roleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Role> findByRoleId(@PathVariable Long roleId) {
        return new ResponseEntity<>(roleService.findByRoleId(roleId), HttpStatus.OK);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Role>> getRoleWithPermissions(@NotNull final Pageable pageable) {
        return new ResponseEntity<>(roleService.getRoleWithPermissions(pageable), HttpStatus.OK);
    }

    @GetMapping(value = "{roleId}/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<AppUser>> findAllAppUsersByRoleId(@PathVariable Long roleId, @NotNull final Pageable pageable) {
        return new ResponseEntity<>(roleService.findAllAppUsersByRoleId(roleId, pageable), HttpStatus.OK);
    }
}
