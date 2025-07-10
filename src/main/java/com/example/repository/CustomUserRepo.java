package com.example.repository;

import com.example.entity.AppUser;

import java.util.List;

public interface CustomUserRepo {
    List<AppUser> getAllUsersByEmailUsingEntityManager(String email, int start, int max);

    List<AppUser> getAllUsersByEmailUsingSessionFactory(String email);

    void saveTestUserData();
}