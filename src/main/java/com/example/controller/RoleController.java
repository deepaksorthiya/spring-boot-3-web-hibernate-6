package com.example.controller;

import com.example.constants.KeyConstants;
import com.example.entity.Role;
import com.example.repository.RoleRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(KeyConstants.API_PREFIX + "/roles")
public class RoleController {

    private RoleRepository roleRepository;

    public RoleController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Role>> findAll(@NotNull final Pageable pageable,
                                              @RequestHeader HttpHeaders httpHeaders) {
        return new ResponseEntity<>(roleRepository.getRoleWithPermissions(pageable), HttpStatus.OK);
    }
}
