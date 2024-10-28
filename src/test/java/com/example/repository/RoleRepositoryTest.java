package com.example.repository;

import com.example.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RoleRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void testSaveRoleSuccess() {
        Role r1 = new Role(null, "ROLE_USER", "role user", null, null);
        entityManager.persist(r1);
        assertNotNull(r1.getRoleId());
        Role r2 = roleRepository.findById(r1.
                getRoleId()).orElse(null);
        assertNotNull(r2.getRoleId());
        assertEquals(r1.getRoleId(), r2.getRoleId());
        assertEquals(r1.
                getRoleName(), r2.getRoleName());
        assertEquals(r1.getRoleDesc(), r2.getRoleDesc());
    }

    @Test
    void getRoleWithPermissions() {
    }

    @Test
    void findByRoleId() {
    }
}