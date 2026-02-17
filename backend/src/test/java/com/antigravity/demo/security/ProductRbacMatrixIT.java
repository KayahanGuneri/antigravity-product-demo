package com.antigravity.demo.security;

import com.antigravity.demo.dto.ProductDTOs.ProductCreateRequest;
import com.antigravity.demo.dto.ProductDTOs.ProductUpdateRequest;
import com.antigravity.demo.model.Product;
import com.antigravity.demo.repository.ProductRepository;
import com.antigravity.demo.testsupport.JwtTestTokens;
import com.antigravity.demo.testsupport.PostgresTestContainerConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProductRbacMatrixIT extends PostgresTestContainerConfig {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID existingProductId;

    @BeforeEach
    void setup() {
        // Create a product for ID-based tests
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("Existing Product");
        product.setDescription("Description");
        product.setPrice(BigDecimal.TEN);
        product.setStock(10);
        product.setCreatedAt(Instant.now());

        product = productRepository.save(product);
        existingProductId = product.getId();
    }

    // --- GET /api/products (List) ---

    @Test
    void getProducts_NoToken_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getProducts_UserToken_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/products")
                .header("Authorization", "Bearer " + JwtTestTokens.createUserToken()))
                .andExpect(status().isOk());
    }

    @Test
    void getProducts_AdminToken_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/products")
                .header("Authorization", "Bearer " + JwtTestTokens.createAdminToken()))
                .andExpect(status().isOk());
    }

    // --- GET /api/products/{id} (Single) ---

    @Test
    void getProduct_NoToken_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/products/" + existingProductId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getProduct_UserToken_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/products/" + existingProductId)
                .header("Authorization", "Bearer " + JwtTestTokens.createUserToken()))
                .andExpect(status().isOk());
    }

    @Test
    void getProduct_AdminToken_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/products/" + existingProductId)
                .header("Authorization", "Bearer " + JwtTestTokens.createAdminToken()))
                .andExpect(status().isOk());
    }

    // --- POST /api/products (Create) ---

    @Test
    void createProduct_NoToken_ShouldReturn401() throws Exception {
        ProductCreateRequest request = new ProductCreateRequest("New", "Desc", BigDecimal.ONE, 1);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createProduct_UserToken_ShouldReturn403() throws Exception {
        ProductCreateRequest request = new ProductCreateRequest("New", "Desc", BigDecimal.ONE, 1);

        mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + JwtTestTokens.createUserToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createProduct_AdminToken_ShouldReturn201() throws Exception {
        ProductCreateRequest request = new ProductCreateRequest("New Admin Product", "Desc", BigDecimal.ONE, 1);

        mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + JwtTestTokens.createAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    // --- PUT /api/products/{id} (Update) ---

    @Test
    void updateProduct_NoToken_ShouldReturn401() throws Exception {
        ProductUpdateRequest request = new ProductUpdateRequest("Upd", "Desc", BigDecimal.ONE, 1);

        mockMvc.perform(put("/api/products/" + existingProductId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateProduct_UserToken_ShouldReturn403() throws Exception {
        ProductUpdateRequest request = new ProductUpdateRequest("Upd", "Desc", BigDecimal.ONE, 1);

        mockMvc.perform(put("/api/products/" + existingProductId)
                .header("Authorization", "Bearer " + JwtTestTokens.createUserToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateProduct_AdminToken_ShouldReturn200() throws Exception {
        ProductUpdateRequest request = new ProductUpdateRequest("Updated Name", "Desc", BigDecimal.ONE, 1);

        mockMvc.perform(put("/api/products/" + existingProductId)
                .header("Authorization", "Bearer " + JwtTestTokens.createAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // --- DELETE /api/products/{id} (Delete) ---

    @Test
    void deleteProduct_NoToken_ShouldReturn401() throws Exception {
        mockMvc.perform(delete("/api/products/" + existingProductId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteProduct_UserToken_ShouldReturn403() throws Exception {
        mockMvc.perform(delete("/api/products/" + existingProductId)
                .header("Authorization", "Bearer " + JwtTestTokens.createUserToken()))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteProduct_AdminToken_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/products/" + existingProductId)
                .header("Authorization", "Bearer " + JwtTestTokens.createAdminToken()))
                .andExpect(status().isNoContent());
    }
}
