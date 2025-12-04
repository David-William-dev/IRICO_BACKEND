package com.irico.backend.task.dto;

import com.irico.backend.task.LocationDetailsModel;
import com.irico.backend.task.TaskModel;
import lombok.Data;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Data
public class TaskResponse {
    private String id;
    private String name;
    private String designation;
    private TaskModel.TaskStatus status;
    private ZonedDateTime createdAt;
    private String pickedBy;
    private String pickedByName;
    private ZonedDateTime pickedAt;
    private ZonedDateTime startedAt;
    private ZonedDateTime completedAt;
    private List<LocationDetailsModel> locationDetails;

    public TaskResponse(TaskModel task) {
        this.id = task.getId();
        this.name = task.getName();
        this.designation = task.getDesignation();
        this.status = task.getStatus();
        this.pickedBy = task.getPickedBy();
        this.createdAt = task.getCreatedAt().atZone(ZoneId.of("Asia/Kolkata"));
        this.pickedByName = task.getPickedByName();
        this.pickedAt = task.getPickedAt().atZone(ZoneId.of("Asia/Kolkata"));
        this.startedAt = task.getStartedAt().atZone(ZoneId.of("Asia/Kolkata"));
        this.completedAt = task.getCompletedAt().atZone(ZoneId.of("Asia/Kolkata"));
        this.locationDetails = task.getLocationDetails();
    }
}