package com.irico.backend.organizations;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationRepository extends MongoRepository<OrganizationModel, String> {

    Optional<OrganizationModel> findByName(String name);

    Optional<OrganizationModel> findByEmail(String email);

    Optional<OrganizationModel> findByContact(String contact);
}