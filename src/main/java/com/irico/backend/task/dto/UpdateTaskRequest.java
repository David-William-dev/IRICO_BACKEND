package com.irico.backend.task.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTaskRequest {
    @NotNull(message = "Task details are required")
    @Valid
    private TaskRequest task;
}