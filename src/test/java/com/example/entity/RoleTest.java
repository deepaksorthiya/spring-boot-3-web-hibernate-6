package com.example.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    @DisplayName("Should create Role using 2-argument constructor")
    void testTwoArgConstructor() {
        Role role = new Role("ROLE_MANAGER", "Manager role");
        assertEquals("ROLE_MANAGER", role.getRoleName());
        assertEquals("Manager role", role.getRoleDesc());
        assertNotNull(role.getPermissions());
        assertNotNull(role.getAppUsers());
    }

    @Test
    @DisplayName("Should verify getters and setters")
    void testGettersAndSetters() {
        Role role = new Role();
        role.setRoleId(10L);
        role.setRoleName("ROLE_EDITOR");
        role.setRoleDesc("Editor description");

        Set<AppUser> users = new HashSet<>();
        AppUser user = new AppUser();
        user.setUserId(1L);
        users.add(user);
        role.setAppUsers(users);

        Set<Permission> permissions = new HashSet<>();
        Permission permission = new Permission("EDIT", "Edit permission");
        permission.setPermissionId(5L);
        permissions.add(permission);
        role.setPermissions(permissions);

        assertEquals(10L, role.getRoleId());
        assertEquals("ROLE_EDITOR", role.getRoleName());
        assertEquals("Editor description", role.getRoleDesc());
        assertEquals(users, role.getAppUsers());
        assertEquals(permissions, role.getPermissions());
    }

    @Test
    @DisplayName("Should manage bidirectional permission relationship with addPermission and removePermission")
    void testAddAndRemovePermission() {
        Role role = new Role("ROLE_ADMIN", "Admin");
        Permission permission = new Permission("DELETE", "Delete permission");

        role.addPermission(permission);
        assertTrue(role.getPermissions().contains(permission));
        assertTrue(permission.getRoles().contains(role));

        role.removePermission(permission);
        assertFalse(role.getPermissions().contains(permission));
        assertFalse(permission.getRoles().contains(role));
    }

    @Test
    @DisplayName("Should test equals and hashCode based on roleId")
    void testEqualsAndHashCode() {
        Role r1 = new Role();
        r1.setRoleId(1L);
        Role r2 = new Role();
        r2.setRoleId(1L);
        Role r3 = new Role();
        r3.setRoleId(2L);

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1, r3);
        assertNotEquals(r1, null);
        assertNotEquals(r1, new Object());
        assertEquals(r1, r1);

        Role rNull1 = new Role();
        Role rNull2 = new Role();
        assertEquals(rNull1, rNull2);
        assertEquals(rNull1.hashCode(), rNull2.hashCode());
    }

    @Test
    @DisplayName("Should test all-arguments constructor and toString")
    void testAllArgsConstructorAndToString() {
        Set<AppUser> users = new HashSet<>();
        Set<Permission> permissions = new HashSet<>();
        Role role = new Role(5L, "ROLE_SUPER", "Super User", users, permissions);

        assertEquals(5L, role.getRoleId());
        assertThat(role.toString()).contains("roleId=5", "roleName=ROLE_SUPER", "roleDesc=Super User");
    }
}
