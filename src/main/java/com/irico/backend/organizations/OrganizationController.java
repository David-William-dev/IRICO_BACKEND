package com.irico.backend.organizations;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.irico.backend.organizations.dto.OrganizationRequest;
import com.irico.backend.organizations.dto.OrganizationResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/organizations")
@EnableMethodSecurity
@Configuration
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    @PostMapping
    @Operation(summary = "Create a new organization", description = "Registers a new organization with name, email, and contact.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Organization created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or duplicate data"),
            @ApiResponse(responseCode = "409", description = "Name or email already exists")
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<OrganizationResponse> createOrganization(
        @Valid @RequestBody OrganizationRequest request) {
        OrganizationResponse response = organizationService.createOrganization(request);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get organization by ID", description = "Retrieves full details of an organization by its unique ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Organization not found")
    })
    @PreAuthorize("hasAuthority('MANAGER') or hasAuthority('EMPLOYEE') or hasAuthority('ADMIN')")
    public ResponseEntity<OrganizationResponse> getOrganizationById(@PathVariable String id) {
        OrganizationResponse response = organizationService.getOrganizationById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get organization by name", description = "Finds organization by its registered name (case-sensitive).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Organization not found")
    })
    @PreAuthorize("hasAuthority('MANAGER') or hasAuthority('EMPLOYEE') or hasAuthority('ADMIN')")
    public ResponseEntity<OrganizationResponse> getOrganizationByName(@PathVariable String name) {
        OrganizationResponse response = organizationService.getOrganizationByName(name);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get organization by email", description = "Finds organization by its registered email address.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Organization not found")
    })
    @PreAuthorize("hasAuthority('MANAGER') or hasAuthority('EMPLOYEE') or hasAuthority('ADMIN')")
    public ResponseEntity<OrganizationResponse> getOrganizationByEmail(@PathVariable String email) {
        OrganizationResponse response = organizationService.getOrganizationByEmail(email);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all organizations", description = "Returns a list of all registered organizations.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @PreAuthorize("hasAuthority('MANAGER') or hasAuthority('EMPLOYEE') or hasAuthority('ADMIN')")
    public ResponseEntity<List<OrganizationResponse>> getAllOrganizations() {
        List<OrganizationResponse> orgs = organizationService.getAllOrganizations();
        return ResponseEntity.ok(orgs);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update organization details", description = "Updates name, email, or contact of an existing organization.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Organization updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or duplicate data"),
            @ApiResponse(responseCode = "404", description = "Organization not found")
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<OrganizationResponse> updateOrganization(
            @PathVariable String id,
            @Valid @RequestBody OrganizationRequest request) {
        OrganizationResponse response = organizationService.updateOrganization(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an organization", description = "Permanently removes an organization from the system.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Organization deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Organization not found")
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteOrganization(@PathVariable String id) {
        organizationService.deleteOrganization(id);
        return ResponseEntity.noContent().build();
    }
}