package com.example.controller;

import com.example.constants.KeyConstants;
import com.example.entity.Permission;
import com.example.repository.PermissionRepository;
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
@RequestMapping(KeyConstants.API_PREFIX + "/permissions")
public class PermissionController {

    private PermissionRepository permissionRepository;

    public PermissionController(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Permission>> findAll(@NotNull final Pageable pageable,
                                                    @RequestHeader HttpHeaders httpHeaders) {
        return new ResponseEntity<>(permissionRepository.findAll(pageable), HttpStatus.OK);
    }
}
