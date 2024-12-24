package com.example.repository;

import com.example.entity.AppUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAppUserSuccess() {
        AppUser r1 = new AppUser(null, "u1@gmgg.com", "u1dfdsf", "u1dfdsf", "fsdsdf", "dxvfsf", null);
        entityManager.persist(r1);
        assertNotNull(r1.getUserId());
        AppUser r2 = userRepository.findById(r1.
                getUserId()).orElse(null);
        assertNotNull(r2.
                getUserId());
        assertEquals(r1.
                getUserId(), r2.getUserId());
        assertEquals(r1.
                getEmail(), r2.getEmail());
        assertEquals(r1.getPassword(), r2.getPassword());
    }

    @Test
    void getAllUserWithRoles() {
    }

    @Test
    void getAllUserWithRolesAndPermissions() {
    }

    @Test
    void getAllCountUserWithRolesAndPermissions() {
    }

    @Test
    void findAll() {
    }

    @Test
    void findAllBy() {
    }

    @Test
    void findByUserId() {
    }
}