package com.irico.backend.organizations;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import java.time.Instant;

@Document(collection = "organization")
@Data
public class OrganizationModel {

    @Id
    private String id;
    private String name;        
    private String email;       
    private String contact;    
    @CreatedDate 
    private Instant createdAt;
}