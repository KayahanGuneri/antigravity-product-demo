package com.antigravity.demo.controller;

import com.antigravity.demo.dto.ProductDTOs.*;
import com.antigravity.demo.exception.GlobalExceptionHandler;
import com.antigravity.demo.exception.ProductNotFoundException;
import com.antigravity.demo.security.JwtService;
import com.antigravity.demo.repository.UserRepository;
import com.antigravity.demo.service.ProductService;
import com.antigravity.demo.testsupport.ProductTestData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class ProductControllerWebMvcTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private ProductService productService;

        @MockBean
        private JwtService jwtService;

        @MockBean
        private UserRepository userRepository;

        @Test
        void getAllProducts_shouldReturnList() throws Exception {
                ProductResponse p1 = new ProductResponse(UUID.randomUUID(), "P1", "D1", null, null, null);
                ProductResponse p2 = new ProductResponse(UUID.randomUUID(), "P2", "D2", null, null, null);
                when(productService.getAllProducts()).thenReturn(Arrays.asList(p1, p2));

                mockMvc.perform(get("/api/products"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.length()").value(2))
                                .andExpect(jsonPath("$[0].name").value("P1"))
                                .andExpect(jsonPath("$[1].name").value("P2"));

                verify(productService, times(1)).getAllProducts();
        }

        @Test
        void getProduct_shouldReturnProduct_WhenExists() throws Exception {
                UUID id = UUID.randomUUID();
                ProductResponse response = new ProductResponse(id, "Test", "Desc", null, null, null);
                when(productService.getProduct(id)).thenReturn(response);

                mockMvc.perform(get("/api/products/{id}", id))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(id.toString()))
                                .andExpect(jsonPath("$.name").value("Test"));

                verify(productService, times(1)).getProduct(id);
        }

        @Test
        void getProduct_shouldReturn404_WhenNotFound() throws Exception {
                UUID id = UUID.randomUUID();
                when(productService.getProduct(id)).thenThrow(new ProductNotFoundException("Not found"));

                mockMvc.perform(get("/api/products/{id}", id))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").value(404))
                                .andExpect(jsonPath("$.message").value("Not found"));

                verify(productService, times(1)).getProduct(id);
        }

        @Test
        void createProduct_shouldReturn201() throws Exception {
                ProductCreateRequest request = ProductTestData.createRequest();
                ProductResponse response = new ProductResponse(UUID.randomUUID(), request.name(), request.description(),
                                request.price(), request.stock(), null);
                when(productService.createProduct(any(ProductCreateRequest.class))).thenReturn(response);

                mockMvc.perform(post("/api/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.name").value(request.name()));

                verify(productService, times(1)).createProduct(any(ProductCreateRequest.class));
        }

        @Test
        void updateProduct_shouldReturn200() throws Exception {
                UUID id = UUID.randomUUID();
                ProductUpdateRequest request = ProductTestData.updateRequest();
                ProductResponse response = new ProductResponse(id, request.name(), request.description(),
                                request.price(), request.stock(), null);
                when(productService.updateProduct(eq(id), any(ProductUpdateRequest.class))).thenReturn(response);

                mockMvc.perform(put("/api/products/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name").value(request.name()));

                verify(productService, times(1)).updateProduct(eq(id), any(ProductUpdateRequest.class));
        }

        @Test
        void deleteProduct_shouldReturn204() throws Exception {
                UUID id = UUID.randomUUID();
                doNothing().when(productService).deleteProduct(id);

                mockMvc.perform(delete("/api/products/{id}", id))
                                .andExpect(status().isNoContent());

                verify(productService, times(1)).deleteProduct(id);
        }
}
