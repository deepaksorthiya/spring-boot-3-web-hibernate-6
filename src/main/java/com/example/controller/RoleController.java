package com.example.controller;

import com.example.constants.KeyConstants;
import com.example.entity.Role;
import com.example.global.exceptions.ResourceNotFoundException;
import com.example.repository.RoleRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(KeyConstants.API_PREFIX + "/roles")
public class RoleController {

    private RoleRepository roleRepository;

    public RoleController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @GetMapping(value = "{roleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Role> getUserById(@PathVariable long roleId) {
        return new ResponseEntity<>(roleRepository.findById(roleId).orElseThrow(() -> new ResourceNotFoundException(roleId)), HttpStatus.OK);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Role>> findAll(@NotNull final Pageable pageable,
                                              @RequestHeader HttpHeaders httpHeaders) {
        return new ResponseEntity<>(roleRepository.getRoleWithPermissions(pageable), HttpStatus.OK);
    }

    @DeleteMapping(value = "{roleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteRoleById(@PathVariable long roleId) {
        roleRepository.deleteById(roleId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
