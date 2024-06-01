package com.example.controller;

import com.example.entity.AppUser;
import com.example.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MiscController {

    private final UserRepository userRepository;

    public MiscController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping(value = "/misc", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AppUser>> getAllUsersByEmail(@RequestParam String email) {
        List<AppUser> users = userRepository.getAllUsersByEmail(email);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}
