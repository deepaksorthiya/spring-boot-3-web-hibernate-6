package com.example.repository;

import com.example.entity.AppUser;

import java.util.List;

public interface CustomUserRepo {
    List<AppUser> getAllUsersByEmail(String email);
}
