package com.irico.backend.job.dto;

import com.irico.backend.job.JobModel;
import lombok.Data;

import java.time.Instant;

@Data
public class JobPageSummaryResponse {
    private String id;
    private String name;
    private Instant createdAt;
    private String organizationName;
    private String status; 
    private Integer totalTasks;
    private Integer completedTasks;
    private String createdBy;

    public JobPageSummaryResponse(JobModel job) {
        this.id = job.getId();
        this.name = job.getName();
        this.createdAt = job.getCreatedAt();
        this.organizationName = job.getOrganizationName();
        this.status = job.getStatus() != null ? job.getStatus().name() : "PENDING";
        this.totalTasks = job.getTotalTasks();
        this.completedTasks = job.getCompletedTasks();
        this.createdBy = job.getCreatedBy();
    }
}