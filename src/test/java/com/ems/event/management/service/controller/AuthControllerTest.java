package com.ems.event.management.service.controller;

import com.ems.event.management.service.entity.User;
import com.ems.event.management.service.repository.UserRepository;
import com.ems.event.management.service.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(AuthControllerTest.MockConfig.class) // Import Mock Configuration
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void shouldLoginAndReturnToken() throws Exception {
        String email = "test@example.com";
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(email);

        // Mock behavior for finding a user and generating JWT token
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(any(UUID.class), any(String.class))).thenReturn("mocked-jwt-token");

        mockMvc.perform(post("/api/v1/auth/login")
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk()) // Expect HTTP 200 status
                .andExpect(content().string("mocked-jwt-token")); // Expect the mocked JWT token
    }

    @Test
    void shouldReturnErrorWhenUserNotFound() throws Exception {
        String email = "nonexistent@example.com";

        // Mock behavior for when no user is found
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/v1/auth/login")
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isNotFound()) // Expect HTTP 404 status
                .andExpect(content().string("User not found")); // Expect error message
    }

    @TestConfiguration
    static class MockConfig {

        @Bean
        public UserRepository userRepository() {
            return Mockito.mock(UserRepository.class); // Mock UserRepository
        }

        @Bean
        public JwtUtil jwtUtil() {
            return Mockito.mock(JwtUtil.class); // Mock JwtUtil
        }
    }
}
