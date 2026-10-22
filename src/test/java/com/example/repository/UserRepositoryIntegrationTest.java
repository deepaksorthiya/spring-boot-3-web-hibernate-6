package com.example.repository;

import com.example.entity.AppUser;
import com.example.entity.Permission;
import com.example.entity.Role;
import com.example.entity.UserGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private AppUser user1;
    private AppUser user2;
    private Role roleAdmin;
    private Role roleUser;
    private Permission permRead;
    private Permission permWrite;
    private UserGroup groupDev;
    private String user1Email;
    private String user2Email;
    private String adminRoleName;
    private String devGroupName;

    @BeforeEach
    void setUp() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        user1Email = "alice_" + suffix + "@example.com";
        user2Email = "bob_" + suffix + "@example.com";
        adminRoleName = "ROLE_ADMIN_" + suffix;
        String userRoleName = "ROLE_USER_" + suffix;
        String readPermName = "READ_" + suffix;
        String writePermName = "WRITE_" + suffix;
        devGroupName = "Devs_" + suffix;

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

        groupDev = new UserGroup(devGroupName, "Dev Group");
        entityManager.persist(groupDev);

        user1 = new AppUser(user1Email, "Password@123", "Password@123", "Alice", "Smith");
        user1.addRole(roleAdmin);
        user1.addToGroup(groupDev);
        entityManager.persist(user1);

        user2 = new AppUser(user2Email, "Password@123", "Password@123", "Bob", "Jones");
        user2.addRole(roleUser);
        entityManager.persist(user2);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("Should find user by ID with entity graph")
    void testFindByUserId() {
        Optional<AppUser> found = userRepository.findByUserId(user1.getUserId());
        assertTrue(found.isPresent());
        assertEquals(user1Email, found.get().getEmail());
        assertThat(found.get().getRoles()).hasSize(1);
    }

    @Test
    @DisplayName("Should get all users with roles and pagination")
    void testGetAllUserWithRoles() {
        Page<AppUser> page = userRepository.getAllUserWithRoles(PageRequest.of(0, 50));
        assertThat(page.getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("Should get all users with roles and permissions")
    void testGetAllUserWithRolesAndPermissions() {
        Page<AppUser> page = userRepository.getAllUserWithRolesAndPermissions(PageRequest.of(0, 50));
        assertThat(page.getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("Should count all users with roles and permissions")
    void testGetAllCountUserWithRolesAndPermissions() {
        long count = userRepository.getAllCountUserWithRolesAndPermissions();
        assertThat(count).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("Should find all users with pagination and entity graph")
    void testFindAllWithEntityGraph() {
        Page<AppUser> page = userRepository.findAll(PageRequest.of(0, 50));
        assertThat(page.getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("Should find allBy with pagination and roles entity graph")
    void testFindAllBy() {
        Page<AppUser> page = userRepository.findAllBy(PageRequest.of(0, 50));
        assertThat(page.getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("Should get single user with roles query")
    void testGetUserWithRoles() {
        Optional<AppUser> found = userRepository.getUserWithRoles(user1.getUserId());
        assertTrue(found.isPresent());
        assertThat(found.get().getRoles()).extracting(Role::getRoleName).contains(adminRoleName);
    }

    @Test
    @DisplayName("Should find users by role ID")
    void testFindByRolesRoleId() {
        Page<AppUser> page = userRepository.findByRolesRoleId(roleAdmin.getRoleId(), PageRequest.of(0, 10));
        assertThat(page.getContent()).hasSize(1);
        assertEquals(user1Email, page.getContent().getFirst().getEmail());
    }

    @Test
    @DisplayName("Should find user by ID with groups")
    void testFindByUserIdWithGroups() {
        Optional<AppUser> found = userRepository.findByUserIdWithGroups(user1.getUserId());
        assertTrue(found.isPresent());
        assertThat(found.get().getUserGroups()).extracting(UserGroup::getGroupName).contains(devGroupName);
    }

    @Test
    @DisplayName("Should find all users with groups")
    void testFindAllWithGroups() {
        List<AppUser> users = userRepository.findAllWithGroups();
        assertThat(users).isNotEmpty();
        AppUser foundUser1 = users.stream().filter(u -> u.getUserId().equals(user1.getUserId())).findFirst().orElseThrow();
        assertThat(foundUser1.getUserGroups()).isNotEmpty();
    }
}
