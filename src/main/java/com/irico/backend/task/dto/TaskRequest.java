package com.irico.backend.task.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;


@Data
public class TaskRequest {

    @NotBlank(message = "Task name is required")
    private String name;

    @NotBlank(message = "Designation is required")
    private String designation;

    @NotBlank(message = "Technique type is required")
    @Pattern(regexp = "^(PT|RT|MT|UT)$", message = "Invalid technique type. Must be PT, RT, MT, or UT")
    private String techniqueType;


    @NotNull(message = "At least one location is required")
    @Valid
    private List<LocationDetailsRequest> locationsDetails;


}
