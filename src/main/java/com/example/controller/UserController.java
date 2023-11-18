package com.example.controller;

import com.example.constants.KeyConstants;
import com.example.entity.AppUser;
import com.example.global.exceptions.ResourceNotFoundException;
import com.example.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(KeyConstants.API_PREFIX + "/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AppUser> createUser(@RequestBody @Validated AppUser appUser) {
        return new ResponseEntity<>(userRepository.save(appUser), HttpStatus.CREATED);
    }

    @GetMapping(value = "{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AppUser> getUserById(@PathVariable long userId) {
        return new ResponseEntity<>(userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(userId)), HttpStatus.OK);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<AppUser>> findAll(@NotNull final Pageable pageable) {
        return new ResponseEntity<>(userRepository.findAll(pageable), HttpStatus.OK);
    }

    @GetMapping(value = "count", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> count() {
        return new ResponseEntity<>(userRepository.getAllCountUserWithRolesAndPermissions(), HttpStatus.OK);
    }

    @GetMapping(value = "findAll", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<AppUser>> getAllUserWithRolesNotPermissions(@NotNull final Pageable pageable) {
        return new ResponseEntity<>(userRepository.findAllBy(pageable), HttpStatus.OK);
    }

    @GetMapping(value = "roles", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<AppUser>> getAllUserWithRoles(@NotNull final Pageable pageable) {
        return new ResponseEntity<>(userRepository.getAllUserWithRoles(pageable), HttpStatus.OK);
    }

    @GetMapping(value = "roles/permissions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<AppUser>> getAllUserWithRolesAndPermissions(@NotNull final Pageable pageable) {
        return new ResponseEntity<>(userRepository.getAllUserWithRolesAndPermissions(pageable), HttpStatus.OK);
    }
}
