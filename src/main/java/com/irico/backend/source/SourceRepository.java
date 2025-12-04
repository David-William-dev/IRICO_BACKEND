package com.irico.backend.source;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SourceRepository extends MongoRepository<SourceModel, String> {
    Optional<SourceModel> findBySourceName(String sourceName);
}