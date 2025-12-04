package com.irico.backend.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Email is mandatory")
    @Email
    String email;
    @NotBlank(message = "Password is mandatory")
    String password;
}