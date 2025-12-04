package com.irico.backend.user;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {
    @Id
    private String userId;

    @Field("name")
    @NotBlank(message = "User name cannot be blank")
    private String userName;

    @Field("email")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @Field("password")
    @Size(min = 6, max = 20, message = "Password must be at least 6 characters long")
    @NotBlank(message = "Password cannot be blank")
    private String password;

    @Field("role")
    @NotBlank(message = "Role cannot be blank")
    private Set<String> roles = new HashSet<>();


}
