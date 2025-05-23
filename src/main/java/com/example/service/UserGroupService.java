package com.example.service;

import com.example.entity.AppUser;
import com.example.entity.UserGroup;
import com.example.repository.UserGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        groupRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Set<AppUser> getGroupUsers(Long groupId) {
        UserGroup group = groupRepository.findByIdWithUsers(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        return group.getAppUsers();
    }
}
