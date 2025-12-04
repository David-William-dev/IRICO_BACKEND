package com.irico.backend.job;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface JobRepository extends MongoRepository<JobModel, String> {

    List<JobModel> findByOrganizationName(String organizationName);

    @Query("{ 'createdBy': ?0, 'createdAt': { $gte: ?1, $lte: ?2 } }")
    Page<JobModel> findByCreatedByAndCreatedAtBetween(
            String createdBy,
            Instant startDate,
            Instant endDate,
            Pageable pageable);

    @Query("{ 'tasks': { $elemMatch: { 'pickedBy': ?0 } }, 'createdAt': { $gte: ?1, $lte: ?2 } }")
    Page<JobModel> findByTasksPickedByAndCreatedAtBetween(
            String pickedBy,
            Instant startDate,
            Instant endDate,
            Pageable pageable
    );

    List<JobModel> findByStatus(JobModel.JobStatus status);

    Page<JobModel> findByOrganizationNameAndCreatedAtBetween(
            String organizationName,
            Instant startDate,
            Instant endDate,
            Pageable pageable);
}