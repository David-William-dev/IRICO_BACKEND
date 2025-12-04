package com.irico.backend.job.dto;

import com.irico.backend.job.JobModel;
import com.irico.backend.source.dto.SourceResponse;
import lombok.Data;
import com.irico.backend.task.dto.TaskResponse;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class JobResponse {

    private String id;
    private String name;
    private String mediaUrl;
    private String createdBy;
    private ZonedDateTime createdAt;
    private String organizationName;
    private BigDecimal totalSquareInch;
    private BigDecimal totalRatePerJob;
    private Integer totalTasks;
    private String createdByName;
    private Integer completedTasks;
    private JobModel.JobStatus status;
    private List<TaskResponse> tasks;
    private List<SourceResponse> sourceUsed;



    public JobResponse(JobModel job) {
        this.id = job.getId();
        this.name = job.getName();
        this.mediaUrl = job.getMediaUrl();
        this.createdByName = job.getCreatedByName();
        this.createdAt = job.getCreatedAt().atZone(ZoneId.of("Asia/Kolkata"));
        this.organizationName = job.getOrganizationName();
        this.totalSquareInch = job.getTotalSquareInch();
        this.totalRatePerJob = job.getTotalRatePerJob();
        this.totalTasks = job.getTotalTasks();
        this.completedTasks = job.getCompletedTasks();
        this.status = job.getStatus();
        this.tasks = job.getTasks().stream()
                .map(TaskResponse::new)
                .collect(Collectors.toList());
        this.sourceUsed = job.getSourceUsedInSquareInch().stream().map(SourceResponse::new)
                .collect(Collectors.toList());
    }
}