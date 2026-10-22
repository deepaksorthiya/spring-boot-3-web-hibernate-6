package com.example.controller;

import com.example.config.WebAppConfig;
import com.example.entity.AppUser;
import com.example.global.exceptions.UserControllerAdviceHandler;
import com.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MiscController.class)
@Import({WebAppConfig.class, UserControllerAdviceHandler.class})
class MiscControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    private AppUser sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new AppUser("user@test.com", "Pass@1234", "Pass@1234", "A", "B");
        sampleUser.setUserId(1L);
    }

    @Test
    @DisplayName("GET /getAllUsersByEmailUsingEntityManager - Should return users from EntityManager")
    void testGetAllUsersByEmailUsingEntityManager() throws Exception {
        when(userRepository.getAllUsersByEmailUsingEntityManager("user@test.com", 0, 10))
                .thenReturn(List.of(sampleUser));

        mockMvc.perform(get("/getAllUsersByEmailUsingEntityManager")
                        .param("email", "user@test.com")
                        .param("start", "0")
                        .param("max", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email").value("user@test.com"));

        verify(userRepository, times(1))
                .getAllUsersByEmailUsingEntityManager("user@test.com", 0, 10);
    }

    @Test
    @DisplayName("GET /getAllUsersByEmailUsingSessionFactory - Should return users from SessionFactory")
    void testGetAllUsersByEmailUsingSessionFactory() throws Exception {
        when(userRepository.getAllUsersByEmailUsingSessionFactory("user@test.com"))
                .thenReturn(List.of(sampleUser));

        mockMvc.perform(get("/getAllUsersByEmailUsingSessionFactory")
                        .param("email", "user@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email").value("user@test.com"));

        verify(userRepository, times(1))
                .getAllUsersByEmailUsingSessionFactory("user@test.com");
    }
}
