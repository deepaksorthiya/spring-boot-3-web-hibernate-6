package com.example.repository;

import com.example.dto.AppUserDto;
import com.example.entity.AppUser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.util.List;

public class CustomUserRepoImpl implements CustomUserRepo {

    @PersistenceContext
    private EntityManager entityManager;

    private static AppUser convert(AppUserDto appUserDto) {
        return new AppUser(appUserDto.userId(), appUserDto.email(), appUserDto.password(), appUserDto.firstName(), appUserDto.lastName(), null);
    }

    @Override
    public List<AppUser> getAllUsersByEmail(String email) {
        long startTime = System.currentTimeMillis();
        // Execute the query
        Query query = entityManager.createQuery("SELECT new com.example.dto.AppUserDto(u.userId, u.email, u.password, u.firstName, u.lastName) FROM AppUser u where u.email like :email");
        query.setParameter("email", "%" + email + "%");
        List<AppUserDto> users = query.getResultList();
        entityManager.close();
        long endTime = System.currentTimeMillis();
        System.out.println(users);
        System.out.println("Time taken : " + (endTime - startTime));
        return users.stream().map(CustomUserRepoImpl::convert).toList();
    }
}
