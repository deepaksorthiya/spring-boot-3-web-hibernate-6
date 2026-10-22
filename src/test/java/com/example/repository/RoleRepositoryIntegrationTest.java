package com.example.repository;

import com.example.entity.Permission;
import com.example.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class RoleRepositoryIntegrationTest {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Role roleAdmin;
    private Role roleUser;
    private Permission permRead;
    private Permission permWrite;
    private String adminRoleName;
    private String userRoleName;

    @BeforeEach
    void setUp() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        adminRoleName = "ROLE_ADMIN_" + suffix;
        userRoleName = "ROLE_USER_" + suffix;
        String readPermName = "READ_" + suffix;
        String writePermName = "WRITE_" + suffix;

        permRead = new Permission(readPermName, "Read permission");
        permWrite = new Permission(writePermName, "Write permission");
        entityManager.persist(permRead);
        entityManager.persist(permWrite);

        roleAdmin = new Role(adminRoleName, "Admin role");
        roleAdmin.addPermission(permRead);
        roleAdmin.addPermission(permWrite);
        entityManager.persist(roleAdmin);

        roleUser = new Role(userRoleName, "User role");
        roleUser.addPermission(permRead);
        entityManager.persist(roleUser);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("Should get role with permissions with entity graph")
    void testGetRoleWithPermissions() {
        Page<Role> page = roleRepository.getRoleWithPermissions(PageRequest.of(0, 20));
        assertThat(page.getContent()).isNotEmpty();
        Role fetchedAdmin = page.getContent().stream()
                .filter(r -> r.getRoleName().equals(adminRoleName))
                .findFirst()
                .orElseThrow();
        assertThat(fetchedAdmin.getPermissions()).hasSize(2);
    }

    @Test
    @DisplayName("Should find role by roleId with permissions")
    void testFindByRoleId() {
        Optional<Role> found = roleRepository.findByRoleId(roleAdmin.getRoleId());
        assertTrue(found.isPresent());
        assertEquals(adminRoleName, found.get().getRoleName());
        assertThat(found.get().getPermissions()).hasSize(2);
    }

    @Test
    @DisplayName("Should find roles by permission ID")
    void testFindByPermissionsPermissionId() {
        Page<Role> page = roleRepository.findByPermissionsPermissionId(permWrite.getPermissionId(), PageRequest.of(0, 10));
        assertThat(page.getContent()).hasSize(1);
        assertEquals(adminRoleName, page.getContent().getFirst().getRoleName());
    }

    @Test
    @DisplayName("Should find roles by roleId in set")
    void testFindByRoleIdIn() {
        Set<Role> found = roleRepository.findByRoleIdIn(Set.of(roleAdmin.getRoleId(), roleUser.getRoleId()));
        assertThat(found).hasSize(2);
    }
}
