package com.example.repository;

import com.example.entity.AppUser;
import com.example.entity.UserGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserGroupRepositoryIntegrationTest {

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private TestEntityManager entityManager;

    private UserGroup devGroup;
    private UserGroup opsGroup;
    private AppUser user;
    private String groupNameDev;
    private String userEmail;

    @BeforeEach
    void setUp() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        groupNameDev = "DevTeam_" + suffix;
        String groupNameOps = "OpsTeam_" + suffix;
        userEmail = "dev_" + suffix + "@test.com";

        devGroup = new UserGroup(groupNameDev, "Development Team");
        opsGroup = new UserGroup(groupNameOps, "Operations Team");

        user = new AppUser(userEmail, "Password@123", "Password@123", "Dave", "Developer");
        entityManager.persist(user);

        devGroup.addAppUser(user);
        entityManager.persist(devGroup);
        entityManager.persist(opsGroup);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("Should find user group by group name")
    void testFindByGroupName() {
        Optional<UserGroup> found = userGroupRepository.findByGroupName(groupNameDev);
        assertTrue(found.isPresent());
        assertEquals("Development Team", found.get().getDescription());
    }

    @Test
    @DisplayName("Should find group by ID with users")
    void testFindByIdWithUsers() {
        Optional<UserGroup> found = userGroupRepository.findByIdWithUsers(devGroup.getUserGroupId());
        assertTrue(found.isPresent());
        assertThat(found.get().getAppUsers()).hasSize(1);
        assertEquals(userEmail, found.get().getAppUsers().iterator().next().getEmail());
    }

    @Test
    @DisplayName("Should find all groups with users")
    void testFindAllWithUsers() {
        List<UserGroup> groups = userGroupRepository.findAllWithUsers();
        assertThat(groups).isNotEmpty();
    }
}
