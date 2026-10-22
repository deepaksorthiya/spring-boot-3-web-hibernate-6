package com.example.repository;

import com.example.entity.Permission;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class PermissionRepositoryIntegrationTest {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Should save and find permission by ID")
    void testSaveAndFind() {
        String permName = "PERM_DEL_" + UUID.randomUUID().toString().substring(0, 8);
        Permission permission = new Permission(permName, "Delete resource");
        Permission saved = permissionRepository.save(permission);
        entityManager.flush();
        entityManager.clear();

        Optional<Permission> found = permissionRepository.findById(saved.getPermissionId());
        assertTrue(found.isPresent());
        assertEquals(permName, found.get().getPermissionName());
    }

    @Test
    @DisplayName("Should delete permission by ID")
    void testDeletePermission() {
        String permName = "PERM_AUDIT_" + UUID.randomUUID().toString().substring(0, 8);
        Permission permission = new Permission(permName, "Audit logs");
        Permission saved = permissionRepository.save(permission);
        entityManager.flush();
        entityManager.clear();

        permissionRepository.deleteById(saved.getPermissionId());
        entityManager.flush();

        Optional<Permission> found = permissionRepository.findById(saved.getPermissionId());
        assertThat(found).isEmpty();
    }
}
