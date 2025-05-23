package com.example.controller;

import com.example.entity.AppUser;
import com.example.entity.Role;
import com.example.entity.UserGroup;
import com.example.global.exceptions.ResourceNotFoundException;
import com.example.service.UserService;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("${api.v1.users.prefix}")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AppUser> createAppUser(@RequestBody @Validated AppUser appUser) {
        return new ResponseEntity<>(userService.createUser(appUser), HttpStatus.CREATED);
    }

    @DeleteMapping(value = "{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteAppUserById(@PathVariable Long userId) {
        userService.deleteAppUserById(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AppUser> getAppUserById(@PathVariable Long userId) {
        return new ResponseEntity<>(userService.getAppUserById(userId), HttpStatus.OK);
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

    @GetMapping(value = "{userId}/roles", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Set<Role>> getAppUserRoles(@PathVariable Long userId) {
        return new ResponseEntity<>(userService.getAppUserWithRoles(userId).getRoles(), HttpStatus.OK);
    }

    @GetMapping(value = "roles/permissions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<AppUser>> getAllUserWithRolesAndPermissions(@NotNull final Pageable pageable) {
        return new ResponseEntity<>(userService.getAllUserWithRolesAndPermissions(pageable), HttpStatus.OK);
    }

    @PutMapping(value = "{userId}/roles", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AppUser> assignRolesToUser(@PathVariable Long userId, @RequestBody @NotEmpty Set<Role> roles) {
        return new ResponseEntity<>(userService.assignRolesToUser(userId, roles), HttpStatus.OK);
    }

    @DeleteMapping("{userId}/roles")
    public ResponseEntity<AppUser> removeRolesFromUser(@PathVariable Long userId, @RequestBody @NotEmpty Set<Role> roles) {
        return new ResponseEntity<>(userService.removeRolesFromUser(userId, roles), HttpStatus.OK);
    }

    @GetMapping("/with-groups")
    public ResponseEntity<List<AppUser>> getAllUsersWithGroups() {
        List<AppUser> appUsers = userService.getAllUsersWithGroups();
        return ResponseEntity.ok(appUsers);
    }

    @GetMapping("/{userId}/with-groups")
    public ResponseEntity<AppUser> getUserWithGroupsById(@PathVariable Long userId) {
        AppUser appUser = userService.getUserWithGroupsById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(userId));
        return ResponseEntity.ok(appUser);
    }

    @GetMapping("/{userId}/groups")
    public ResponseEntity<Set<UserGroup>> getUserGroups(@PathVariable Long userId) {
        Set<UserGroup> groups = userService.getUserGroups(userId);
        return ResponseEntity.ok(groups);
    }


    @PostMapping("/{userId}/groups/{groupId}")
    public ResponseEntity<Void> addUserToGroup(@PathVariable Long userId, @PathVariable Long groupId) {
        userService.addUserToGroup(userId, groupId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/groups/{groupId}")
    public ResponseEntity<Void> removeUserFromGroup(@PathVariable Long userId, @PathVariable Long groupId) {
        userService.removeUserFromGroup(userId, groupId);
        return ResponseEntity.ok().build();
    }
}
