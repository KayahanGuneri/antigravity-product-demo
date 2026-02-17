package com.antigravity.demo.security;

import com.antigravity.demo.testsupport.JwtTestTokens;
import com.antigravity.demo.testsupport.PostgresTestContainerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JwtNegativeScenariosIT extends PostgresTestContainerConfig {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void accessProtectedEndpoint_ExpiredToken_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/products")
                .header("Authorization", "Bearer " + JwtTestTokens.createExpiredToken()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accessProtectedEndpoint_TamperedToken_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/products")
                .header("Authorization", "Bearer " + JwtTestTokens.createTamperedToken()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accessProtectedEndpoint_MissingRoleClaim_ShouldReturn403() throws Exception {
        // JwtService.extractRole will return null, SimpleGrantedAuthority("ROLE_null")
        // is created.
        // The token is technically valid (signed, not expired), so it proceeds to
        // authorization which fails with 403.
        mockMvc.perform(get("/api/products")
                .header("Authorization", "Bearer " + JwtTestTokens.createTokenWithoutRole()))
                .andExpect(status().isForbidden());
    }

    @Test
    void accessProtectedEndpoint_WrongSecret_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/products")
                .header("Authorization", "Bearer " + JwtTestTokens.createTokenWithWrongSecret()))
                .andExpect(status().isUnauthorized());
    }
}
