package com.antigravity.demo.security;

import com.antigravity.demo.model.Role;
import com.antigravity.demo.model.User;
import com.antigravity.demo.repository.UserRepository;
import com.antigravity.demo.testsupport.JwtTestTokens;
import com.antigravity.demo.testsupport.PostgresTestContainerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class HardeningVerificationIT extends PostgresTestContainerConfig {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User admin = new User();
        admin.setId(UUID.randomUUID());
        admin.setEmail("admin@example.com");
        admin.setPasswordHash(passwordEncoder.encode("password"));
        admin.setRole(Role.ADMIN);
        admin.setCreatedAt(Instant.now());
        userRepository.save(admin);

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("user@example.com");
        user.setPasswordHash(passwordEncoder.encode("password"));
        user.setRole(Role.USER);
        user.setCreatedAt(Instant.now());
        userRepository.save(user);
    }

    @Test
    void shouldIncludeTraceIdInResponseHeader() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(header().exists("X-Trace-Id"))
                .andExpect(header().string("X-Trace-Id", not(emptyString())));
    }

    @Test
    void shouldReturnStandardizedJsonFor401() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", is("Unauthorized")))
                .andExpect(jsonPath("$.traceId").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path", is("/api/products")));
    }

    @Test
    void shouldReturnStandardizedJsonFor403() throws Exception {
        String userToken = JwtTestTokens.createToken("user@example.com", "ROLE_USER");

        mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test\", \"description\":\"Test\", \"price\":10, \"stock\":10}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error", is("Forbidden")))
                .andExpect(jsonPath("$.traceId").exists())
                .andExpect(jsonPath("$.path", is("/api/products")));
    }

    @Test
    void shouldRejectMaliciousInputWith400() throws Exception {
        String adminToken = JwtTestTokens.createToken("admin@example.com", "ROLE_ADMIN");

        mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{\"name\":\"<script>alert(1)</script>\", \"description\":\"Invalid\", \"price\":10, \"stock\":10}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("contains invalid characters")));
    }
}
