package com.example.repository;

import com.example.entity.AppUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class CustomUserRepoIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should execute saveTestUserData and query via custom repository methods")
    void testCustomRepositoryMethods() {
        userRepository.saveTestUserData();

        // Test getAllUsersByEmailUsingEntityManager
        List<AppUser> adminUsers = userRepository.getAllUsersByEmailUsingEntityManager("admin", 0, 5);
        assertThat(adminUsers).isNotEmpty();
        assertThat(adminUsers.getFirst().getEmail()).contains("admin");

        // Test getAllUsersByEmailUsingSessionFactory
        List<AppUser> usersWithRoles = userRepository.getAllUsersByEmailUsingSessionFactory("admin");
        assertThat(usersWithRoles).isNotEmpty();
        assertThat(usersWithRoles.getFirst().getRoles()).isNotEmpty();
    }
}
