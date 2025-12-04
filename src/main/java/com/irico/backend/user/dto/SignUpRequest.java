package com.irico.backend.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpRequest {
    @NotBlank(message = "Name is mandatory")
    private String name;
    @NotBlank(message = "Email is mandatory")
    @Email
    private String email;
    @NotBlank(message = "Password is mandatory")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;
    @NotBlank(message = "Role is mandatory")
    @Pattern(regexp = "^(GUEST|EMPLOYEE|MANAGER|ADMIN)$", message = "Role must be GUEST, EMPLOYEE, MANAGER, or ADMIN")
    private String role;
}
