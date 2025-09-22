package com.example.service;

import com.example.AbstractBaseTest;
import com.example.entity.AppUser;
import com.example.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserServiceTest extends AbstractBaseTest {

    @InjectMocks
    private UserService userService;

    @Mock
    UserRepository userRepository;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
    }

    @BeforeEach
    void setUp() throws Exception {
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    void createUser() {
        AppUser user = new AppUser("example@example.com", "password", "password", "fname", "lname");
        AppUser expectedUser = new AppUser(100L, "example@example.com", "password", "password", "fname", "lname", Collections.emptySet(), Collections.emptySet());
        when(userRepository.save(user)).thenReturn(expectedUser);
        AppUser returnUser = userService.createUser(user);
        assertEquals("example@example.com", returnUser.getEmail());
        verify(userRepository, times(1)).save(user);
    }
}