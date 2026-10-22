package com.example.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AppUserTest {

    @Test
    @DisplayName("Should create AppUser using 5-argument constructor")
    void testFiveArgConstructor() {
        AppUser user = new AppUser("test@example.com", "Secret@123", "Secret@123", "John", "Doe");
        assertEquals("test@example.com", user.getEmail());
        assertEquals("Secret@123", user.getPassword());
        assertEquals("Secret@123", user.getConfirmPassword());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertNotNull(user.getUserGroups());
        assertNotNull(user.getRoles());
    }

    @Test
    @DisplayName("Should verify getters and setters")
    void testGettersAndSetters() {
        AppUser user = new AppUser();
        user.setUserId(100L);
        user.setEmail("jane@example.com");
        user.setPassword("Pass@1234");
        user.setConfirmPassword("Pass@1234");
        user.setFirstName("Jane");
        user.setLastName("Smith");

        Set<Role> roles = new HashSet<>();
        Role role = new Role("ROLE_ADMIN", "Admin role");
        role.setRoleId(1L);
        roles.add(role);
        user.setRoles(roles);

        Set<UserGroup> groups = new HashSet<>();
        UserGroup group = new UserGroup("Admins", "Admin Group");
        group.setUserGroupId(2L);
        groups.add(group);
        user.setUserGroups(groups);

        assertEquals(100L, user.getUserId());
        assertEquals("jane@example.com", user.getEmail());
        assertEquals("Pass@1234", user.getPassword());
        assertEquals("Pass@1234", user.getConfirmPassword());
        assertEquals("Jane", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals(roles, user.getRoles());
        assertEquals(groups, user.getUserGroups());
    }

    @Test
    @DisplayName("Should manage bidirectional role relationship with addRole and removeRole")
    void testAddAndRemoveRole() {
        AppUser user = new AppUser("user@test.com", "Pass@1234", "Pass@1234", "A", "B");
        Role role = new Role("ROLE_USER", "Regular User");

        user.addRole(role);
        assertTrue(user.getRoles().contains(role));
        assertTrue(role.getAppUsers().contains(user));

        user.removeRole(role);
        assertFalse(user.getRoles().contains(role));
        assertFalse(role.getAppUsers().contains(user));
    }

    @Test
    @DisplayName("Should manage bidirectional group relationship with addToGroup and removeFromGroup")
    void testAddAndRemoveGroup() {
        AppUser user = new AppUser("user@test.com", "Pass@1234", "Pass@1234", "A", "B");
        UserGroup group = new UserGroup("Developers", "Dev Team");

        user.addToGroup(group);
        assertTrue(user.getUserGroups().contains(group));
        assertTrue(group.getAppUsers().contains(user));

        user.removeFromGroup(group);
        assertFalse(user.getUserGroups().contains(group));
        assertFalse(group.getAppUsers().contains(user));
    }

    @Test
    @DisplayName("Should test equals and hashCode based on userId")
    void testEqualsAndHashCode() {
        AppUser user1 = new AppUser();
        user1.setUserId(1L);
        user1.setEmail("u1@test.com");

        AppUser user2 = new AppUser();
        user2.setUserId(1L);
        user2.setEmail("u2@test.com");

        AppUser user3 = new AppUser();
        user3.setUserId(2L);

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1, user3);
        assertNotEquals(user1, null);
        assertNotEquals(user1, new Object());
        assertEquals(user1, user1);

        AppUser userNullId1 = new AppUser();
        AppUser userNullId2 = new AppUser();
        assertEquals(userNullId1, userNullId2);
        assertEquals(userNullId1.hashCode(), userNullId2.hashCode());
    }

    @Test
    @DisplayName("Should test all-arguments constructor and toString")
    void testAllArgsConstructorAndToString() {
        Set<UserGroup> groups = new HashSet<>();
        Set<Role> roles = new HashSet<>();
        AppUser user = new AppUser(1L, "admin@test.com", "Pass@1234", "Pass@1234", "Admin", "User", groups, roles);

        assertEquals(1L, user.getUserId());
        assertThat(user.toString()).contains("userId=1", "email=admin@test.com", "firstName=Admin", "lastName=User");
    }
}
