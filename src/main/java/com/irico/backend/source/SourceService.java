package com.irico.backend.source;

import com.irico.backend.source.dto.SourceDeltaRequest;
import com.irico.backend.source.dto.SourceRequest;
import com.irico.backend.source.dto.SourceResponse;
import com.irico.backend.source.exception.SourceAlreadyExistsException;
import com.irico.backend.source.exception.SourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class SourceService {

    @Autowired
    private SourceRepository sourceRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public SourceResponse addSourceCount(String sourceId, SourceDeltaRequest request) {

        // Use $inc to atomically add the amount
        Update update = new Update().inc("sourceCount", request.getSourceCount());
        com.mongodb.client.result.UpdateResult result = mongoTemplate.updateFirst(
                new Query(Criteria.where("_id").is(sourceId)),
                update,
                SourceModel.class);

        if (result.getMatchedCount() == 0) {
            throw new SourceNotFoundException("Source not found with ID: " + sourceId);
        }

        // Fetch and return updated document
        SourceModel updated = sourceRepository.findById(sourceId)
                .orElseThrow(() -> new SourceNotFoundException("Source not found after update: " + sourceId));

        return new SourceResponse(updated);
    }

    public SourceResponse subtractSourceCount(String sourceId, SourceDeltaRequest request) {

        // Validate that we won't go below zero
        SourceModel current = sourceRepository.findById(sourceId)
                .orElseThrow(() -> new SourceNotFoundException("Source not found with ID: " + sourceId));

        BigDecimal newCount = current.getSourceCount().subtract(request.getSourceCount());
        if (newCount.intValue() < 0) {
            throw new IllegalArgumentException("Cannot subtract: would result in negative sourceCount (" +
                    current.getSourceCount() + " - " + request.getSourceCount() + " = " + newCount + ")");
        }

        // Safe to subtract — use $inc with negative value
        Update update = new Update().inc("sourceCount", request.getSourceCount().multiply(new BigDecimal(-1)));
        com.mongodb.client.result.UpdateResult result = mongoTemplate.updateFirst(
                new Query(Criteria.where("_id").is(sourceId)),
                update,
                SourceModel.class);

        if (result.getMatchedCount() == 0) {
            throw new SourceNotFoundException("Source not found with ID: " + sourceId);
        }

        SourceModel updated = sourceRepository.findById(sourceId)
                .orElseThrow(() -> new SourceNotFoundException("Source not found after update: " + sourceId));

        return new SourceResponse(updated);
    }

    public SourceResponse createSource(SourceRequest request) {

        Optional<SourceModel> existing = sourceRepository.findBySourceName(request.getSourceName());
        if (existing.isPresent()) {
            throw new SourceAlreadyExistsException(
                    "Source with name '" + request.getSourceName() + "' already exists.");
        }

        SourceModel model = new SourceModel();
        model.setSourceName(request.getSourceName());
        model.setSourceCount(request.getSourceCount().setScale(2,RoundingMode.HALF_UP));
        model.setSourceRate(request.getSourceRate().setScale(2,RoundingMode.HALF_UP));

        SourceModel saved = sourceRepository.save(model);
        return new SourceResponse(saved);
    }

    public SourceResponse getSourceById(String sourceId) {
        return sourceRepository.findById(sourceId)
                .map(SourceResponse::new)
                .orElseThrow(() -> new SourceNotFoundException("Source not found with ID: " + sourceId));
    }

    public SourceResponse getSourceByName(String sourceName) {
        return sourceRepository.findBySourceName(sourceName)
                .map(SourceResponse::new)
                .orElseThrow(() -> new SourceNotFoundException("Source not found with name: " + sourceName));
    }

    public List<SourceResponse> getAllSources() {
        return sourceRepository.findAll().stream()
                .map(SourceResponse::new)
                .toList();
    }

    public SourceResponse updateSource(String sourceId, SourceRequest request) {

        SourceModel existing = sourceRepository.findById(sourceId)
                .orElseThrow(() -> new SourceNotFoundException("Source not found with ID: " + sourceId));

        // Prevent name change to an existing name
        if (!existing.getSourceName().equals(request.getSourceName())) {
            Optional<SourceModel> duplicate = sourceRepository.findBySourceName(request.getSourceName());
            if (duplicate.isPresent()) {
                throw new SourceAlreadyExistsException(
                        "Source with name '" + request.getSourceName() + "' already exists.");
            }
        }

        existing.setSourceName(request.getSourceName());
        existing.setSourceCount(request.getSourceCount().setScale(2,RoundingMode.HALF_UP));
        existing.setSourceRate(request.getSourceRate().setScale(2,RoundingMode.HALF_UP));

        SourceModel updated = sourceRepository.save(existing);
        return new SourceResponse(updated);
    }

    public void deleteSource(String sourceId) {
        if (!sourceRepository.existsById(sourceId)) {
            throw new SourceNotFoundException("Source not found with ID: " + sourceId);
        }
        sourceRepository.deleteById(sourceId);
    }
}