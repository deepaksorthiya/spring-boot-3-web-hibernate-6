package com.example.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PermissionTest {

    @Test
    @DisplayName("Should create Permission using 2-argument constructor")
    void testTwoArgConstructor() {
        Permission permission = new Permission("EXECUTE", "Execute permission");
        assertEquals("EXECUTE", permission.getPermissionName());
        assertEquals("Execute permission", permission.getPermissionDesc());
        assertNotNull(permission.getRoles());
    }

    @Test
    @DisplayName("Should verify getters and setters")
    void testGettersAndSetters() {
        Permission permission = new Permission();
        permission.setPermissionId(20L);
        permission.setPermissionName("EXPORT");
        permission.setPermissionDesc("Export data");

        Set<Role> roles = new HashSet<>();
        Role role = new Role("ROLE_EXPORT", "Export Role");
        role.setRoleId(1L);
        roles.add(role);
        permission.setRoles(roles);

        assertEquals(20L, permission.getPermissionId());
        assertEquals("EXPORT", permission.getPermissionName());
        assertEquals("Export data", permission.getPermissionDesc());
        assertEquals(roles, permission.getRoles());
    }

    @Test
    @DisplayName("Should test equals and hashCode based on permissionId")
    void testEqualsAndHashCode() {
        Permission p1 = new Permission();
        p1.setPermissionId(1L);
        Permission p2 = new Permission();
        p2.setPermissionId(1L);
        Permission p3 = new Permission();
        p3.setPermissionId(2L);

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
        assertNotEquals(p1, p3);
        assertNotEquals(p1, null);
        assertNotEquals(p1, new Object());
        assertEquals(p1, p1);

        Permission pNull1 = new Permission();
        Permission pNull2 = new Permission();
        assertEquals(pNull1, pNull2);
        assertEquals(pNull1.hashCode(), pNull2.hashCode());
    }

    @Test
    @DisplayName("Should test all-arguments constructor and toString")
    void testAllArgsConstructorAndToString() {
        Set<Role> roles = new HashSet<>();
        Permission permission = new Permission(10L, "IMPORT", "Import data", roles);

        assertEquals(10L, permission.getPermissionId());
        assertThat(permission.toString()).contains("permissionId=10", "permissionName=IMPORT", "permissionDesc=Import data");
    }
}
