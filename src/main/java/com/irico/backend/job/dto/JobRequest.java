package com.irico.backend.job.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.irico.backend.source.dto.SourceRequest;
import com.irico.backend.task.dto.TaskRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class JobRequest {

    @NotBlank(message = "Job name is required")
    private String name;

    // Optional: uncomment if mediaUrl becomes required again
    // @NotBlank(message = "Media URL is required")
    private String mediaUrl;

    @NotBlank(message = "Created by is required")
    private String createdBy;

    @NotBlank(message = "Creator name is required")
    private String createdByName;

    @NotBlank(message = "Organization name is required")
    private String organizationName;

    // @NotNull(message = "Total square inch is required")
    @Positive(message = "Total square inch must be positive")
    private Integer totalSquareInch;

    @Valid
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private List<TaskRequest> tasks;

    @Valid
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private List<SourceRequest> sourceUser;

    public List<TaskRequest> getTasks() {
        return tasks == null ? List.of() : tasks;
    }

    public List<SourceRequest> getSourceUser() {
        return sourceUser == null ? List.of() : sourceUser;
    }

}