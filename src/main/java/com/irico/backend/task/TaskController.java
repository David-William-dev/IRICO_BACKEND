package com.irico.backend.task;

import com.irico.backend.task.dto.*;
import com.irico.backend.job.dto.JobResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs/{jobId}/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping
    @PreAuthorize("hasAuthority('EMPLOYEE') or hasAuthority('MANAGER')")
    @Operation(summary = "Add a new task to a job")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid task data"),
            @ApiResponse(responseCode = "404", description = "Job not found")
    })
    public ResponseEntity<JobResponse> addTask(
            @Parameter(description = "ID of the job", required = true) @PathVariable String jobId,
            @Valid @RequestBody TaskRequest request) {
        JobResponse response = taskService.addTaskToJob(jobId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{taskId}")
    @PreAuthorize("hasAuthority('MANAGER') or hasAuthority('EMPLOYEE')")
    @Operation(summary = "Delete a PENDING task from a job")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Task deletedsuccessfully"),
            @ApiResponse(responseCode = "400", description = "Task is not PENDING"),
            @ApiResponse(responseCode = "404", description = "Job or task not found")

    })

    public ResponseEntity<JobResponse> deleteTask(
            @Parameter(description = "ID of the job", required = true) @PathVariable String jobId,
            @Parameter(description = "ID of the task", required = true) @PathVariable String taskId) {
        JobResponse response = taskService.deleteTaskFromJob(jobId, taskId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{taskId}")
    @PreAuthorize("hasAuthority('MANAGER') or hasAuthority('EMPLOYEE')")
    @Operation(summary = "Update an existing task in a job")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Task updatedsuccessfully"),
            @ApiResponse(responseCode = "400", description = "Invalid task data"),
            @ApiResponse(responseCode = "404", description = "Job or task not found")

    })

    public ResponseEntity<JobResponse> updateTask(
            @Parameter(description = "ID of the job", required = true) @PathVariable String jobId,
            @Parameter(description = "ID of the task", required = true) @PathVariable String taskId,
            @Valid @RequestBody UpdateTaskRequest request) {
        JobResponse response = taskService.updateTaskInJob(jobId, taskId,
                request.getTask());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{taskId}/assign")
    @Operation(summary = "Assign a task to an inspector")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task assigned"),
            @ApiResponse(responseCode = "404", description = "Job or task not found"),
            @ApiResponse(responseCode = "409", description = "Task already assigned")
    })
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<JobResponse> assignTask(
            @Parameter(description = "ID of the job", required = true) @PathVariable String jobId,
            @Parameter(description = "ID of the task", required = true) @PathVariable String taskId,
            @Parameter(description = "Employee ID to assign to", required = true) @RequestParam String assignedToId,
            @Parameter(description = "Employee Name to assign to", required = true) @RequestParam String assignedToName) {
        JobResponse response = taskService.assignTask(jobId, taskId, assignedToId, assignedToName);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{taskId}/start")
    @Operation(summary = "Start a task")
    @ApiResponses({ @ApiResponse(responseCode = "200", description = "Task started"),
            @ApiResponse(responseCode = "404", description = "Job or task not found"),
            @ApiResponse(responseCode = "409", description = "Task not assigned or wronginspector")

    })
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<JobResponse> startTask(
            @Parameter(description = "ID of the job", required = true) @PathVariable String jobId,
            @Parameter(description = "ID of the task", required = true) @PathVariable String taskId,
            @Parameter(description = "Employee ID (must match assigned)", required = true) @RequestParam String employeeId) {
        JobResponse response = taskService.startTask(jobId, taskId, employeeId);
        return ResponseEntity.ok(response);
    }
}

// // --- START TASK ---

// // --- COMPLETE TASK ---
// @PostMapping("/{taskId}/complete")
// @Operation(summary = "Complete a task")
// @ApiResponses({
// @ApiResponse(responseCode = "200", description = "Task completed"),
// @ApiResponse(responseCode = "404", description = "Job or task not found"),
// @ApiResponse(responseCode = "409", description = "Task not in progress or
// wrong inspector")
// })
// @PreAuthorize("hasAuthority('EMPLOYEE')")
// public ResponseEntity<JobResponse> completeTask(
// @Parameter(description = "ID of the job", required = true) @PathVariable
// String jobId,
// @Parameter(description = "ID of the task", required = true) @PathVariable
// String taskId,
// @Parameter(description = "Employee ID (must match assigned)", required =
// true) @RequestParam String employeeId) {
// JobResponse response = taskService.completeTask(jobId, taskId, employeeId);
// return ResponseEntity.ok(response);
// }
// }