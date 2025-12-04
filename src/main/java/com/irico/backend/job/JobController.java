package com.irico.backend.job;

import com.irico.backend.job.dto.JobPageResponse;
import com.irico.backend.job.dto.JobRequest;
import com.irico.backend.job.dto.JobResponse;
// import com.irico.backend.job.dto.JobSummaryResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
// import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@EnableMethodSecurity
@Configuration
public class JobController {

        @Autowired
        private JobService jobService;

        @GetMapping("/organization/{orgName}")
        @Operation(summary = "Get all jobs by organization", description = "Returns all jobs linked to a specific organization.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Success")
        })
        @PreAuthorize("hasAuthority('MANAGER') or hasAuthority('EMPLOYEE') or hasAuthority('ADMIN')")
        public ResponseEntity<List<JobResponse>> getJobsByOrganization(@PathVariable String orgName) {
                List<JobResponse> jobs = jobService.getJobsByOrganization(orgName);
                return ResponseEntity.ok(jobs);
        }

        @PostMapping
        @Operation(summary = "Create a new inspection job", description = "Creates a job with multiple tasks that consume sources and track NDT activities.")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Job created successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input or missing fields"),
                        @ApiResponse(responseCode = "409", description = "Duplicate data or invalid technique type")
        })
        @PreAuthorize("hasAuthority('MANAGER') or hasAuthority('ADMIN')")
        public ResponseEntity<JobResponse> createJob(@Valid @RequestBody JobRequest request) {
                JobResponse response = jobService.createJob(request);
                return ResponseEntity.status(201).body(response);
        }

        @DeleteMapping("/{id}")
        @PreAuthorize("hasAuthority('MANAGER') or hasAuthority('ADMIN')")
        @Operation(summary = "Delete a job by ID", description = "Deletes the job and all its tasks.Only allowed for PENDING jobs (optional).")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Job deleted successfully"),
                        @ApiResponse(responseCode = "403", description = "Not authorized to delete this job"),
                        @ApiResponse(responseCode = "404", description = "Job not found")
        })
        public ResponseEntity<Void> deleteJob(@PathVariable String id) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                String currentUserId = ((UserDetails) auth.getPrincipal()).getUsername();

                jobService.deleteJobById(id, currentUserId);
                return ResponseEntity.noContent().build();
        }

        @GetMapping("/{id}")
        @Operation(summary = "Get job by ID", description = "Returns full job details including all tasks and flim data.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Success"),
                        @ApiResponse(responseCode = "404", description = "Job not found")
        })
        @PreAuthorize("hasAuthority('MANAGER') or hasAuthority('EMPLOYEE') or hasAuthority('ADMIN')")
        public ResponseEntity<JobResponse> getJob(@PathVariable String id) {
                JobResponse response = jobService.getJobById(id);
                return ResponseEntity.ok(response);
        }

        @GetMapping("/organization")
        @Operation(summary = "Get recently created jobs (last 7 days)", description = """
                        Returns paginated list of jobs created within the last 7 days.
                        Supports sorting by 'createdAt' (default).
                        Use page=0, size=10 for default view.
                        """, parameters = {
                        @Parameter(name = "page", description = "Page number (0-indexed)", example = "0"),
                        @Parameter(name = "size", description = "Items per page (max 100)", example = "10"),
                        @Parameter(name = "sortBy", description = "Sort field (only 'createdAt' allowed)", example = "createdAt")
        })
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Success"),
                        @ApiResponse(responseCode = "400", description = "Invalid page or size parameters")
        })
        @PreAuthorize("hasAuthority('MANAGER') or hasAuthority('EMPLOYEE') or hasAuthority('ADMIN')")
        public ResponseEntity<JobPageResponse> getJobsCreatedInLastWeek(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(required = false) Instant startDate,
                        @RequestParam(required = false) Instant endDate,
                        @RequestParam(required = true) String organizationName,
                        @RequestParam(required = false) String sortBy) {

                // Validate pagination limits
                if (page < 0)
                        throw new IllegalArgumentException("Page number cannot be negative");
                if (size < 1 || size > 100)
                        throw new IllegalArgumentException("Size must be between 1 and 100");

                JobPageResponse response = jobService.getJobsCreatedInLastWeek(
                                organizationName,
                                startDate,
                                endDate,
                                page,
                                size,
                                sortBy);
                return ResponseEntity.ok(response);
        }

        @GetMapping("/created-by/{userId}")
        public ResponseEntity<JobPageResponse> getJobsByCreatedBy(
                        @PathVariable String userId,
                        @RequestParam(required = false) Instant startDate,
                        @RequestParam(required = false) Instant endDate,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "createdAt") String sortBy,
                        @RequestParam(defaultValue = "desc") String sortDirection) {

                return ResponseEntity.ok(
                                jobService.getJobsByCreatedBy(userId, startDate, endDate, page, size, sortBy,
                                                sortDirection));
        }

        @GetMapping("/picked-by/{userId}")
        public ResponseEntity<JobPageResponse> getJobsByPickedBy(
                        @PathVariable String userId,
                        @RequestParam(required = false) Instant startDate,
                        @RequestParam(required = false) Instant endDate,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "createdAt") String sortBy,
                        @RequestParam(defaultValue = "desc") String sortDirection) {

                return ResponseEntity.ok(
                                jobService.getJobsByPickedBy(userId, startDate, endDate, page, size, sortBy,
                                                sortDirection));
        }

}

// @GetMapping("/organization/{orgName}")
// @Operation(summary = "Get all jobs by organization", description = "Returns
// all jobs linked to a specific organization.")
// @ApiResponses({
// @ApiResponse(responseCode = "200", description = "Success")
// })
// @PreAuthorize("hasAuthority('MANAGER') or hasAuthority('EMPLOYEE') or
// hasAuthority('ADMIN')")
// public ResponseEntity<List<JobResponse>> getJobsByOrganization(@PathVariable
// String orgName) {
// List<JobResponse> jobs = jobService.getJobsByOrganization(orgName);
// return ResponseEntity.ok(jobs);
// }

// @GetMapping("/manager/{managerId}")
// @Operation(summary = "Get all jobs by manager", description = "Returns all
// jobs created by a specific manager.")
// @ApiResponses({
// @ApiResponse(responseCode = "200", description = "Success")
// })
// @PreAuthorize("hasAuthority('MANAGER') or hasAuthority('ADMIN')")
// public ResponseEntity<List<JobResponse>> getJobsByManager(@PathVariable
// String managerId) {
// List<JobResponse> jobs = jobService.getJobsByManager(managerId);
// return ResponseEntity.ok(jobs);
// }

// @GetMapping("/last-week")
// @Operation(summary = "Get recently created jobs (last 7 days)", description =
// """
// Returns paginated list of jobs created within the last 7 days.
// Supports sorting by 'createdAt' (default).
// Use page=0, size=10 for default view.
// """, parameters = {
// @Parameter(name = "page", description = "Page number (0-indexed)", example =
// "0"),
// @Parameter(name = "size", description = "Items per page (max 100)", example =
// "10"),
// @Parameter(name = "sortBy", description = "Sort field (only 'createdAt'
// allowed)", example = "createdAt")
// })
// @ApiResponses({
// @ApiResponse(responseCode = "200", description = "Success"),
// @ApiResponse(responseCode = "400", description = "Invalid page or size
// parameters")
// })
// @PreAuthorize("hasAuthority('MANAGER') or hasAuthority('EMPLOYEE') or
// hasAuthority('ADMIN')")
// public ResponseEntity<JobPageResponse> getJobsCreatedInLastWeek(
// @RequestParam(defaultValue = "0") int page,
// @RequestParam(defaultValue = "10") int size,
// @RequestParam(required = false) String sortBy) {

// // Validate pagination limits
// if (page < 0)
// throw new IllegalArgumentException("Page number cannot be negative");
// if (size < 1 || size > 100)
// throw new IllegalArgumentException("Size must be between 1 and 100");

// JobPageResponse response = jobService.getJobsCreatedInLastWeek(page, size,
// sortBy);
// return ResponseEntity.ok(response);
// }

// @GetMapping
// @PreAuthorize("hasAuthority('MANAGER') or hasAuthority('ADMIN')")
// @Operation(summary = "Get list of all jobs (summary)", description = "Returns
// job ID, name, creation date, org, and status.")
// public ResponseEntity<List<JobSummaryResponse>> getAllJobsSummary() {
// List<JobSummaryResponse> jobs = jobService.getAllJobsSummary();
// return ResponseEntity.ok(jobs);
// }

// }