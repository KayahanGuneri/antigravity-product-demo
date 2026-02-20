package com.antigravity.demo.controller;

import com.antigravity.demo.dto.AuthRequest;
import com.antigravity.demo.dto.AuthResponse;
import com.antigravity.demo.exception.GlobalExceptionHandler;
import com.antigravity.demo.repository.UserRepository;
import com.antigravity.demo.security.JwtService;
import com.antigravity.demo.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AuthControllerWebMvcTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private AuthService authService;

        @MockBean
        private JwtService jwtService;

        @MockBean
        private UserRepository userRepository;

        @Test
        void register_shouldReturn200() throws Exception {
                AuthRequest.RegisterRequest request = new AuthRequest.RegisterRequest();
                request.setEmail("test@test.com");
                request.setPassword("password");
                request.setRole("USER");

                AuthResponse response = new AuthResponse("token", 3600, "USER");
                when(authService.register(any(AuthRequest.RegisterRequest.class))).thenReturn(response);

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.accessToken").value("token"))
                                .andExpect(jsonPath("$.role").value("USER"));

                verify(authService, times(1)).register(any());
        }

        @Test
        void login_shouldReturn200_WhenCredentialsValid() throws Exception {
                AuthRequest.LoginRequest request = new AuthRequest.LoginRequest();
                request.setEmail("test@test.com");
                request.setPassword("password");

                AuthResponse response = new AuthResponse("token", 3600, "USER");
                when(authService.login(any(AuthRequest.LoginRequest.class))).thenReturn(response);

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.accessToken").value("token"))
                                .andExpect(jsonPath("$.tokenType").value("Bearer"));

                verify(authService, times(1)).login(any());
        }

        @Test
        void login_shouldReturn401_WhenInvalidCredentials() throws Exception {
                AuthRequest.LoginRequest request = new AuthRequest.LoginRequest();
                request.setEmail("test@test.com");
                request.setPassword("wrongpassword");

                when(authService.login(any(AuthRequest.LoginRequest.class)))
                                .thenThrow(new RuntimeException("Invalid credentials"));

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isUnauthorized());

                verify(authService, times(1)).login(any());
        }

        @Test
        void login_shouldReturn401_WhenUserNotFound() throws Exception {
                AuthRequest.LoginRequest request = new AuthRequest.LoginRequest();
                request.setEmail("unknown@test.com");
                request.setPassword("password");

                when(authService.login(any(AuthRequest.LoginRequest.class)))
                                .thenThrow(new RuntimeException("User not found"));

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isUnauthorized());

                verify(authService, times(1)).login(any());
        }

        @Test
        void register_shouldReturn400_WhenInvalidPayload() throws Exception {
                AuthRequest.RegisterRequest request = new AuthRequest.RegisterRequest();
                request.setEmail("invalid-email");
                request.setPassword("short");

                mockMvc.perform(post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.message").value("Validation failed"))
                                .andExpect(jsonPath("$.fieldErrors").isArray());

                verify(authService, never()).register(any());
        }

        @Test
        void login_shouldReturn400_WhenInvalidEmail() throws Exception {
                AuthRequest.LoginRequest request = new AuthRequest.LoginRequest();
                request.setEmail("not-an-email");
                request.setPassword("password123");

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andDo(print())
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.fieldErrors[0].field").value("email"));

                verify(authService, never()).login(any());
        }
}
