package com.example.service;

import com.example.entity.AppUser;
import com.example.global.exceptions.ResourceNotFoundException;
import com.example.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AppUser createUser(AppUser user) {
        return userRepository.save(user);
    }

    @Transactional
    public void deleteAppUserById(Long userId) {
        AppUser user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(userId));
        // Remove the user from roles to update the join table
        user.setRoles(null);
        userRepository.delete(user);
    }

    public AppUser getAppUserById(Long userId) {
        return userRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException(userId));
    }

    public Page<AppUser> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Long getAllCountUserWithRolesAndPermissions() {
        return userRepository.getAllCountUserWithRolesAndPermissions();
    }

    public Page<AppUser> getAllUserWithRolesNotPermissions(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Page<AppUser> getAllUserWithRoles(Pageable pageable) {
        return userRepository.getAllUserWithRoles(pageable);
    }

    public Page<AppUser> getAllUserWithRolesAndPermissions(Pageable pageable) {
        return userRepository.getAllUserWithRolesAndPermissions(pageable);
    }
}
