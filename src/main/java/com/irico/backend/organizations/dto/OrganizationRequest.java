package com.irico.backend.organizations.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrganizationRequest {

    @NotBlank(message = "Organization name is required")
    private String name;

    @NotBlank(message = "Organization email is required")
    private String email;

    @NotBlank(message = "Organization contact is required")
    private String contact;
}