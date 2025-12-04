package com.irico.backend.user.dto;

import lombok.Data;

@Data
public class UserRequest {
    private String username;
    private String email;
    private String fullName;
    private String role; // GUEST, EMPLOYEE, MANAGER, ADMIN
    private String organizationId; // Optional

    public void validate() {
        if (username == null || username.trim().isEmpty())
            throw new IllegalArgumentException("Username is required");
        if (email == null || !email.contains("@"))
            throw new IllegalArgumentException("Valid email is required");
        if (fullName == null || fullName.trim().isEmpty())
            throw new IllegalArgumentException("Full name is required");
        if (role == null || !UserRole.isValid(role))
            throw new IllegalArgumentException("Role must be GUEST, EMPLOYEE, MANAGER, or ADMIN");
    }

    public enum UserRole {
        GUEST, EMPLOYEE, MANAGER, ADMIN;

        public static boolean isValid(String value) {
            for (UserRole r : values()) {
                if (r.name().equalsIgnoreCase(value)) return true;
            }
            return false;
        }
    }
}