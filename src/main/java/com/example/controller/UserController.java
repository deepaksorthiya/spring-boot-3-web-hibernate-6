package com.example.controller;

import com.example.constants.KeyConstants;
import com.example.entity.AppUser;
import com.example.service.UserService;
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

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AppUser> createUser(@RequestBody @Validated AppUser appUser) {
        return new ResponseEntity<>(userService.createUser(appUser), HttpStatus.CREATED);
    }

    @DeleteMapping(value = "{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteAppUser(@PathVariable Long userId) {
        userService.deleteAppUserById(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AppUser> getUserById(@PathVariable Long userId) {
        return new ResponseEntity<>(userService.getUserById(userId), HttpStatus.OK);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<AppUser>> findAll(@NotNull final Pageable pageable) {
        return new ResponseEntity<>(userService.findAll(pageable), HttpStatus.OK);
    }

    @GetMapping(value = "count", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> getAllCountUserWithRolesAndPermissions() {
        return new ResponseEntity<>(userService.getAllCountUserWithRolesAndPermissions(), HttpStatus.OK);
    }

    @GetMapping(value = "findAll", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<AppUser>> getAllUserWithRolesNotPermissions(@NotNull final Pageable pageable) {
        return new ResponseEntity<>(userService.getAllUserWithRolesNotPermissions(pageable), HttpStatus.OK);
    }

    @GetMapping(value = "roles", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<AppUser>> getAllUserWithRoles(@NotNull final Pageable pageable) {
        return new ResponseEntity<>(userService.getAllUserWithRoles(pageable), HttpStatus.OK);
    }

    @GetMapping(value = "roles/permissions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<AppUser>> getAllUserWithRolesAndPermissions(@NotNull final Pageable pageable) {
        return new ResponseEntity<>(userService.getAllUserWithRolesAndPermissions(pageable), HttpStatus.OK);
    }
}
