package com.example.controller;

import com.example.constants.KeyConstants;
import com.example.entity.AppUser;
import com.example.global.exceptions.ResourceNotFoundException;
import com.example.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(KeyConstants.API_PREFIX + "/users")
public class UserController {

    private final UserRepository userRepository;

    private final EntityManager entityManager;

    public UserController(UserRepository userRepository, EntityManager entityManager) {
        this.userRepository = userRepository;
        this.entityManager = entityManager;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AppUser> save(@RequestBody @Valid AppUser appUser) {
        return new ResponseEntity<>(userRepository.save(appUser), HttpStatus.CREATED);
    }

    @GetMapping(value = "{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AppUser> getUser(@PathVariable long userId) {
        return new ResponseEntity<>(userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(userId)), HttpStatus.OK);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<AppUser>> findAll(@NotNull final Pageable pageable) {
        System.out.println(entityManager);
        Map<String, Object> properties = entityManager.getProperties();
        System.out.println(properties);
        return new ResponseEntity<>(userRepository.findAll(pageable), HttpStatus.OK);
    }

    @GetMapping(value = "new", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<AppUser>> getAllUserWithRolesNotPermissions(@NotNull final Pageable pageable) {
        long count = userRepository.getAllCountUserWithRolesAndPermissions();
        System.out.println(count);
        return new ResponseEntity<>(userRepository.findAllBy(pageable), HttpStatus.OK);
    }
}
