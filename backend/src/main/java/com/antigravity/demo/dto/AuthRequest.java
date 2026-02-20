package com.antigravity.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthRequest {
    public static class RegisterRequest {
        @NotBlank
        @Email
        @Size(max = 320)
        private String email;

        @NotBlank
        @Size(min = 8, max = 72)
        private String password;

        private String role; // Optional, defaults to USER

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }

    public static class LoginRequest {
        @NotBlank
        @Email
        @Size(max = 320)
        private String email;

        @NotBlank
        @Size(min = 8, max = 72)
        private String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
