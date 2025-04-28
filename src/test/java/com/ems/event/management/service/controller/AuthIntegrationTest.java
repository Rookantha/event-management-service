package com.ems.event.management.service.controller;

import com.ems.event.management.service.entity.User;
import com.ems.event.management.service.repository.EventRepository;
import com.ems.event.management.service.repository.UserRepository;
import com.ems.event.management.service.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(AuthIntegrationTest.MockConfig.class) // Import the mock configuration
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private JwtUtil jwtUtil; // Mocked bean will be injected here

    @BeforeEach
    void setUp() {
        eventRepository.deleteAll(); // Clean up dependent records
        userRepository.deleteAll(); // Clean up users

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("testuser@example.com");

        userRepository.save(user);

        when(jwtUtil.generateToken(any(UUID.class), any(String.class))).thenReturn("mocked-jwt-token");
    }

    @Test
    void shouldLoginAndReturnValidJwt() throws Exception {
        String email = "testuser@example.com";

        mockMvc.perform(post("/api/v1/auth/login")
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(not(emptyOrNullString())));
    }

    @TestConfiguration
    static class MockConfig {

        @Bean
        public JwtUtil jwtUtil() {
            return Mockito.mock(JwtUtil.class); // Provide a mock for JwtUtil
        }
    }
}