package com.example.controller;

import com.example.entity.AppUser;
import com.example.entity.UserGroup;
import com.example.global.exceptions.ResourceNotFoundException;
import com.example.service.UserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/groups")
public class UserGroupController {

    private final UserGroupService groupService;

    @Autowired
    public UserGroupController(UserGroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping
    public ResponseEntity<List<UserGroup>> getAllGroups() {
        List<UserGroup> groups = groupService.getAllGroups();
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/with-users")
    public ResponseEntity<List<UserGroup>> getAllGroupsWithUsers() {
        List<UserGroup> groups = groupService.getAllGroupsWithUsers();
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserGroup> getGroupById(@PathVariable Long id) {
        UserGroup group = groupService.getGroupById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
        return ResponseEntity.ok(group);
    }

    @GetMapping("/{id}/with-users")
    public ResponseEntity<UserGroup> getGroupWithUsersById(@PathVariable Long id) {
        UserGroup group = groupService.getGroupWithUsersById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
        return ResponseEntity.ok(group);
    }

    @GetMapping("/{id}/users")
    public ResponseEntity<Set<AppUser>> getGroupUsers(@PathVariable Long id) {
        Set<AppUser> users = groupService.getGroupUsers(id);
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<UserGroup> createGroup(@RequestBody UserGroup groupRequest) {
        UserGroup savedGroup = groupService.saveGroup(groupRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserGroup> updateGroup(@PathVariable Long id, @RequestBody UserGroup groupRequest) {
        UserGroup existingGroup = groupService.getGroupById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
        UserGroup updatedGroup = groupService.saveGroup(existingGroup);

        return ResponseEntity.ok(updatedGroup);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        // Check if group exists
        groupService.getGroupById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
        groupService.deleteGroup(id);
        return ResponseEntity.noContent().build();
    }
}
