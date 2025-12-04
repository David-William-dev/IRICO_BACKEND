package com.irico.backend.organizations;

import com.irico.backend.organizations.dto.OrganizationRequest;
import com.irico.backend.organizations.dto.OrganizationResponse;
import com.irico.backend.organizations.exception.OrganizationNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrganizationService {

    @Autowired
    private OrganizationRepository organizationRepository;

    public OrganizationResponse createOrganization(OrganizationRequest request) {

        // Check for duplicates
        if (organizationRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException("Organization with name '" + request.getName() + "' already exists");
        }

        if (organizationRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Organization with email '" + request.getEmail() + "' already exists");
        }

        OrganizationModel org = new OrganizationModel();
        org.setName(request.getName());
        org.setEmail(request.getEmail());
        org.setContact(request.getContact());

        OrganizationModel saved = organizationRepository.save(org);
        return new OrganizationResponse(saved);
    }

    public OrganizationResponse getOrganizationById(String id) {
        return organizationRepository.findById(id)
                .map(OrganizationResponse::new)
                .orElseThrow(() -> new OrganizationNotFoundException("Organization not found with ID: " + id));
    }

    public OrganizationResponse getOrganizationByName(String name) {
        return organizationRepository.findByName(name)
                .map(OrganizationResponse::new)
                .orElseThrow(() -> new OrganizationNotFoundException("Organization not found with name: " + name));
    }

    public OrganizationResponse getOrganizationByEmail(String email) {
        return organizationRepository.findByEmail(email)
                .map(OrganizationResponse::new)
                .orElseThrow(() -> new OrganizationNotFoundException("Organization not found with email: " + email));
    }

    public List<OrganizationResponse> getAllOrganizations() {
        return organizationRepository.findAll().stream()
                .map(OrganizationResponse::new)
                .toList();
    }

    public OrganizationResponse updateOrganization(String id, OrganizationRequest request) {

        OrganizationModel existing = organizationRepository.findById(id)
                .orElseThrow(() -> new OrganizationNotFoundException("Organization not found with ID: " + id));

        // Check for duplicate name/email (excluding current org)
        if (!existing.getName().equals(request.getName())) {
            if (organizationRepository.findByName(request.getName()).isPresent()) {
                throw new IllegalArgumentException("Organization with name '" + request.getName() + "' already exists");
            }
        }

        if (!existing.getEmail().equals(request.getEmail())) {
            if (organizationRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new IllegalArgumentException(
                        "Organization with email '" + request.getEmail() + "' already exists");
            }
        }

        existing.setName(request.getName());
        existing.setEmail(request.getEmail());
        existing.setContact(request.getContact());

        OrganizationModel updated = organizationRepository.save(existing);
        return new OrganizationResponse(updated);
    }

    public void deleteOrganization(String id) {
        if (!organizationRepository.existsById(id)) {
            throw new OrganizationNotFoundException("Organization not found with ID: " + id);
        }
        organizationRepository.deleteById(id);
    }
}