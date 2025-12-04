package com.irico.backend.organizations.dto;

import com.irico.backend.organizations.OrganizationModel;
import lombok.Data;
import java.time.Instant;

@Data
public class OrganizationResponse {
    private String id;
    private String name;
    private String email;
    private String contact;
    private Instant createdAt;

    public OrganizationResponse(OrganizationModel org) {
        this.id = org.getId();
        this.name = org.getName();
        this.email = org.getEmail();
        this.contact = org.getContact();
        this.createdAt = org.getCreatedAt();
    }
}