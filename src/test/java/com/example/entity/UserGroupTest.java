package com.example.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserGroupTest {

    @Test
    @DisplayName("Should create UserGroup using 2-argument constructor")
    void testTwoArgConstructor() {
        UserGroup group = new UserGroup("Managers", "Management group");
        assertEquals("Managers", group.getGroupName());
        assertEquals("Management group", group.getDescription());
        assertNotNull(group.getAppUsers());
    }

    @Test
    @DisplayName("Should verify getters and setters")
    void testGettersAndSetters() {
        UserGroup group = new UserGroup();
        group.setUserGroupId(15L);
        group.setGroupName("Support");
        group.setDescription("Customer support team");

        Set<AppUser> users = new HashSet<>();
        AppUser user = new AppUser();
        user.setUserId(2L);
        users.add(user);
        group.setAppUsers(users);

        assertEquals(15L, group.getUserGroupId());
        assertEquals("Support", group.getGroupName());
        assertEquals("Customer support team", group.getDescription());
        assertEquals(users, group.getAppUsers());
    }

    @Test
    @DisplayName("Should manage bidirectional user relationship with addAppUser and removeAppUser")
    void testAddAndRemoveAppUser() {
        UserGroup group = new UserGroup("QA", "Quality Assurance");
        AppUser user = new AppUser("qa@test.com", "Pass@1234", "Pass@1234", "Tester", "One");

        group.addAppUser(user);
        assertTrue(group.getAppUsers().contains(user));
        assertTrue(user.getUserGroups().contains(group));

        group.removeAppUser(user);
        assertFalse(group.getAppUsers().contains(user));
        assertFalse(user.getUserGroups().contains(group));
    }

    @Test
    @DisplayName("Should test equals and hashCode based on userGroupId")
    void testEqualsAndHashCode() {
        UserGroup g1 = new UserGroup();
        g1.setUserGroupId(1L);
        UserGroup g2 = new UserGroup();
        g2.setUserGroupId(1L);
        UserGroup g3 = new UserGroup();
        g3.setUserGroupId(2L);

        assertEquals(g1, g2);
        assertEquals(g1.hashCode(), g2.hashCode());
        assertNotEquals(g1, g3);
        assertNotEquals(g1, null);
        assertNotEquals(g1, new Object());
        assertEquals(g1, g1);

        UserGroup gNull1 = new UserGroup();
        UserGroup gNull2 = new UserGroup();
        assertEquals(gNull1, gNull2);
        assertEquals(gNull1.hashCode(), gNull2.hashCode());
    }

    @Test
    @DisplayName("Should test all-arguments constructor and toString")
    void testAllArgsConstructorAndToString() {
        Set<AppUser> users = new HashSet<>();
        UserGroup group = new UserGroup(3L, "Billing", "Finance and billing", users);

        assertEquals(3L, group.getUserGroupId());
        assertThat(group.toString()).contains("userGroupId=3", "groupName=Billing", "description=Finance and billing");
    }
}
