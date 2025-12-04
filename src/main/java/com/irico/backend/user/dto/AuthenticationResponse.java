package com.irico.backend.user.dto;

import lombok.Data;

@Data
public class AuthenticationResponse {
    private String token;
    private String role;
    private String id;
    private String name;
    private String email;
}
