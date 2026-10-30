package com.seatreserve.busbooking.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDtos {

    public static class LoginRequest {
        @NotBlank @Email
        private String email;
        @NotBlank
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RegisterRequest {
        @NotBlank
        private String fullName;
        @NotBlank @Email
        private String email;
        @NotBlank @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;
        private String phone;

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }

    public static class AuthResponse {
        private String token;
        private String type = "Bearer";
        private Long userId;
        private String email;
        private String fullName;
        private String role;

        public AuthResponse() {}

        public AuthResponse(String token, Long userId, String email, String fullName, String role) {
            this.token = token;
            this.userId = userId;
            this.email = email;
            this.fullName = fullName;
            this.role = role;
        }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    public static class UserProfileResponse {
        private Long userId;
        private String email;
        private String fullName;
        private String phone;
        private String role;

        public UserProfileResponse() {}
        public UserProfileResponse(Long userId, String email, String fullName, String phone, String role) {
            this.userId = userId;
            this.email = email;
            this.fullName = fullName;
            this.phone = phone;
            this.role = role;
        }

        public Long getUserId() { return userId; }
        public String getEmail() { return email; }
        public String getFullName() { return fullName; }
        public String getPhone() { return phone; }
        public String getRole() { return role; }
    }
}
