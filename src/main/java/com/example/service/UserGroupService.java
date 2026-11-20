package com.example.service;

import com.example.dto.AppUserDto;
import com.example.entity.UserGroup;
import com.example.global.exceptions.ResourceNotFoundException;
import com.example.mapper.DtoMapper;
import com.example.repository.UserGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserGroupService {

    private final UserGroupRepository groupRepository;

    @Autowired
    public UserGroupService(UserGroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Transactional(readOnly = true)
    public List<UserGroup> getAllGroups() {
        return groupRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<UserGroup> getAllGroupsWithUsers() {
        return groupRepository.findAllWithUsers();
    }

    @Transactional(readOnly = true)
    public Optional<UserGroup> getGroupById(Long id) {
        return groupRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<UserGroup> getGroupWithUsersById(Long id) {
        return groupRepository.findByIdWithUsers(id);
    }

    @Transactional
    public UserGroup saveGroup(UserGroup group) {
        return groupRepository.save(group);
    }

    @Transactional
    public void deleteGroup(Long id) {
        // Check if group exists
        UserGroup userGroup = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
        groupRepository.delete(userGroup);
    }

    @Transactional(readOnly = true)
    public Set<AppUserDto> getGroupUsers(Long groupId) {
        UserGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        return group.getAppUsers().stream().map(DtoMapper::toUserResponse).collect(Collectors.toSet());
    }
}
