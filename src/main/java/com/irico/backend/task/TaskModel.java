package com.irico.backend.task;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;

@Data
public class TaskModel {

    private String id;
    private String name;
    private String designation;
    private TechniqueType techniqueType;
    private TaskStatus status;
    @CreatedDate
    private Instant createdAt;
    private String pickedBy;
    private String pickedByName;
    private LocalDateTime pickedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private List<LocationDetailsModel> locationDetails;

    public Boolean addNewLocationDetail(LocationDetailsModel location) {
        return this.locationDetails.add(location);
    }

    public Boolean removeAllLocations() {
        boolean isCleared = false;
        try {
            this.locationDetails.clear();
            isCleared = true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return isCleared;

    }

    public TaskModel() {
        this.techniqueType = TechniqueType.RT;
        this.id = UUID.randomUUID().toString();
        this.status = TaskStatus.PENDING;
        this.pickedAt = null;
        this.startedAt = null;
        this.completedAt = null;
        this.locationDetails = new ArrayList<>();
    }

    public enum TaskStatus {
        PENDING, ASSIGNED, IN_PROGRESS, COMPLETED
    }

    public enum TechniqueType {
        PT, RT, MT, UT;

        public static TechniqueType isValid(String value) {
            for (TechniqueType type : values()) {
                if (type.name().equalsIgnoreCase(value))
                    return type;
            }
            return TechniqueType.RT;
        }
    }

}