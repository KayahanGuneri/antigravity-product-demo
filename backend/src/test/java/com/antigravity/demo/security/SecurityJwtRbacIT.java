package com.antigravity.demo.security;

import com.antigravity.demo.testsupport.JwtTestTokens;
import com.antigravity.demo.testsupport.PostgresTestContainerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityJwtRbacIT extends PostgresTestContainerConfig {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void accessProtectedEndpoint_NoToken_ShouldReturn401() throws Exception {
        // GET /api/products requires authentication
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accessProtectedEndpoint_InvalidToken_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/products")
                .header("Authorization", "Bearer invalid.token.value"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accessAdminEndpoint_UserToken_ShouldReturn403() throws Exception {
        // POST /api/products is ADMIN only
        String userToken = JwtTestTokens.createUserToken();

        mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test Product\",\"description\":\"Desc\",\"price\":10.0,\"stock\":5}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void accessAdminEndpoint_AdminToken_ShouldReturn201() throws Exception {
        // POST /api/products is ADMIN only
        String adminToken = JwtTestTokens.createAdminToken();

        mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{\"name\":\"Admin Product\",\"description\":\"Created by IT\",\"price\":99.99,\"stock\":100}"))
                .andExpect(status().isCreated());
    }
}
