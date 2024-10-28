package com.example.repository;

import com.example.entity.Permission;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PermissionRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PermissionRepository permissionRepository;

    @Test
    void testSavePermissionSuccess() {
        Permission p1 = new Permission(null, "CREATE_USER", "create user", null);
        entityManager.persist(p1);
        assertNotNull(p1.getPermissionId());
        Permission p2 = permissionRepository.findById(p1.getPermissionId()).orElse(null);
        assertNotNull(p2.getPermissionId());
        assertEquals(p1.getPermissionId(), p2.getPermissionId());
        assertEquals(p1.getPermissionName(), p2.getPermissionName());
        assertEquals(p1.getPermissionDesc(), p2.getPermissionDesc());
    }

}