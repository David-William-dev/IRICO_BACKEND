package com.irico.backend.job;

import com.irico.backend.job.dto.JobPageResponse;
import com.irico.backend.job.dto.JobRequest;
import com.irico.backend.job.dto.JobResponse;
import com.irico.backend.task.FlimModel;
import com.irico.backend.task.LocationDetailsModel;
import com.irico.backend.task.TaskModel;
import com.irico.backend.task.dto.FlimRequest;
import com.irico.backend.task.dto.LocationDetailsRequest;
import com.irico.backend.task.dto.TaskRequest;
import com.irico.backend.task.exception.TaskNotFoundException;
import com.irico.backend.job.exception.JobNotFoundException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
// import java.time.ZoneId;
// import java.time.LocalDateTime;
// import com.irico.backend.source.SourceInJob;
// import com.irico.backend.task.TaskModel;
// import com.irico.backend.task.dto.TaskRequest;
// import com.irico.backend.task.exception.TaskNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
// import java.time.LocalDateTime;
// import java.util.*;
// import java.util.stream.Collectors;
import org.springframework.security.access.AccessDeniedException;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    /* -------------------------------------------------------------------------- */
    /* ------------------------------- JOB SECTION ------------------------------ */
    /* -------------------------------------------------------------------------- */

    /* ------------------------------- CREATE JOB ------------------------------- */

    public JobResponse createJob(JobRequest request) {
        JobModel jobModel = new JobModel();
        jobModel.setName(request.getName());
        jobModel.setMediaUrl(request.getMediaUrl());
        jobModel.setCreatedBy(request.getCreatedBy());
        jobModel.setCreatedByName(request.getCreatedByName());
        jobModel.setOrganizationName(request.getOrganizationName());
        if (!(request.getTasks().isEmpty())) {
            for (TaskRequest taskRequest : request.getTasks()) {
                TaskModel task = taskCreator(taskRequest);
                jobModel.addTask(task);
                for (LocationDetailsRequest location_request : taskRequest.getLocationsDetails()) {
                    jobModel.calculateSource(location_request, task.getId());
                }
            }
        }
        jobModel.calculate();
        JobModel saved = jobRepository.save(jobModel);
        return new JobResponse(saved);
    }

    /* -------------------------------------------------------------------------- */

    /* ---------------------- DELETE THE EXISTING JOB BY ID --------------------- */

    public void deleteJobById(String jobId, String currentUserId) {

        JobModel job = findJob(jobId);
        if (job.getStatus() != JobModel.JobStatus.PENDING) {
            throw new IllegalStateException("Cannot delete non-PENDING job");
        }

        if (!job.getCreatedBy().equals(currentUserId)) {
            throw new AccessDeniedException("You can only delete your own jobs");
        }

        jobRepository.deleteById(jobId);
    }

    /* -------------------------------------------------------------------------- */

    /* ------------------------------ GET JOB BY ID ----------------------------- */

    public JobResponse getJobById(String jobId) {
        JobModel job = findJob(jobId);
        return new JobResponse(job);
    }

    /* -------------------------------------------------------------------------- */

    /* --------------------- GET JOB LIST BY ORGANIZATION ---------------------- */

    public List<JobResponse> getJobsByOrganization(String organizationName) {
        return jobRepository.findByOrganizationName(organizationName).stream()
                .map(JobResponse::new)
                .toList();
    }

    /* -------------------------------------------------------------------------- */

    /*
     * -------- LIST JOB BY THE PAGE WIHT RESPECT TO THE ORGANIZATION NAME -------
     */

    public JobPageResponse getJobsCreatedInLastWeek(
            String organizationName,
            Instant startDate,
            Instant endDate,
            int page,
            int size,
            String sortBy) {

        sortBy = (sortBy == null || sortBy.trim().isEmpty()) ? "createdAt" : sortBy;
        if (!"createdAt".equals(sortBy)) {
            sortBy = "createdAt";
        }

        PageRequest pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, sortBy) // DESC for latest first
        );

        Instant now = Instant.now();
        if (startDate == null) {
            startDate = now.minusSeconds(7 * 24 * 60 * 60); // last 7 days
        }
        if (endDate == null) {
            endDate = now;
        }

        Page<JobModel> jobPage = jobRepository.findByOrganizationNameAndCreatedAtBetween(
                organizationName,
                startDate,
                endDate,
                pageable);

        return new JobPageResponse(jobPage);
    }

    /* -------------------------------------------------------------------------- */

    /* ------------- GET LIST OF JOBS WHICH RESPECT TO CREATED BY ID ------------ */

    public JobPageResponse getJobsByCreatedBy(
            String createdBy,
            Instant startDate,
            Instant endDate,
            int page,
            int size,
            String sortBy,
            String sortDirection) {

        // Default values
        sortBy = (sortBy == null || sortBy.trim().isEmpty()) ? "createdAt" : sortBy;
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDirection)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Instant now = Instant.now();
        if (startDate == null)
            startDate = now.minusSeconds(7 * 24 * 60 * 60); // last 7 days
        if (endDate == null)
            endDate = now;

        Page<JobModel> jobPage = jobRepository.findByCreatedByAndCreatedAtBetween(
                createdBy, startDate, endDate, pageable);

        return new JobPageResponse(jobPage);
    }

    /* -------------------------------------------------------------------------- */

    /* ------------- GET LIST OF JOBS WHICH RESPECT TO PICKED BY ID ------------ */

    public JobPageResponse getJobsByPickedBy(
            String pickedBy,
            Instant startDate,
            Instant endDate,
            int page,
            int size,
            String sortBy,
            String sortDirection) {

        sortBy = (sortBy == null || sortBy.trim().isEmpty()) ? "createdAt" : sortBy;
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDirection)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Instant now = Instant.now();
        if (startDate == null)
            startDate = now.minusSeconds(7 * 24 * 60 * 60);
        if (endDate == null)
            endDate = now;

        Page<JobModel> jobPage = jobRepository.findByTasksPickedByAndCreatedAtBetween(
                pickedBy, startDate, endDate, pageable);

        return new JobPageResponse(jobPage);
    }

    /* -------------------------------------------------------------------------- */

    /* -------------------------------------------------------------------------- */
    /* ------------------------------ TASK SECTION ------------------------------ */
    /* -------------------------------------------------------------------------- */

    /* ---------------------- ADD NEW TASK TO EXISTING JOB ---------------------- */

    public JobResponse addTaskToJob(String jobId, TaskRequest taskRequest) {
        JobModel jobModel = findJob(jobId);
        TaskModel task = taskCreator(taskRequest);
        jobModel.addTask(task);
        for (LocationDetailsRequest location_request : taskRequest.getLocationsDetails()) {
            jobModel.calculateSource(location_request, task.getId());
        }
        jobModel.calculate();
        JobModel saved = jobRepository.save(jobModel);
        return new JobResponse(saved);
    }

    /* -------------------------------------------------------------------------- */

    /* ---------------- DELETE EXISTING TASK FROM A EXISTING JOB ---------------- */

    public JobResponse deleteTaskFromJob(String jobId, String taskId) {
        JobModel job = findJob(jobId);
        Boolean is_task_removed = job.removeTask(taskId);
        if (is_task_removed)
            job.calculate();
        JobModel saved = jobRepository.save(job);
        return new JobResponse(saved);
    }

    /* -------------------------------------------------------------------------- */

    /* -------------------------- UPDATE EXISTING TASK -------------------------- */

    public JobResponse updateTaskInJob(String jobId, String taskId, TaskRequest taskRequest) {
        JobModel job = findJob(jobId);
        TaskModel task = findTaskInJob(job, taskId);
        task.setName(taskRequest.getName());
        task.setDesignation(taskRequest.getDesignation());
        task.setTechniqueType(TaskModel.TechniqueType.valueOf(taskRequest.getTechniqueType().toUpperCase()));
        if (task.removeAllLocations()) {
            for (LocationDetailsRequest locationDetail : taskRequest.getLocationsDetails()) {
                task.addNewLocationDetail(locationDetailsCreator(locationDetail));
            }
        }
        JobModel saved = jobRepository.save(job);
        return new JobResponse(saved);
    }

    /* -------------------------------------------------------------------------- */

    /* ------------------------ ASSING TASK TO A EMPLOYEE ----------------------- */

    public JobResponse assignTask(String jobId, String taskId, String assignedToId, String assignedToName) {
        JobModel job = findJob(jobId);
        TaskModel task = findTaskInJob(job, taskId);

        if (!task.getStatus().equals(TaskModel.TaskStatus.PENDING)) {
            throw new IllegalStateException("Task must be PENDING to assign");
        }

        task.setPickedBy(assignedToId);
        task.setPickedByName(assignedToName);
        task.setStatus(TaskModel.TaskStatus.ASSIGNED);
        task.setPickedAt(LocalDateTime.now());

        jobRepository.save(job);
        return new JobResponse(job);
    }

    /* -------------------------------------------------------------------------- */

    /* ------------------- START TASK BY THE ASSIGNED EMPLOYEE ------------------ */

    public JobResponse startTask(String jobId, String taskId, String employeeId) {
        JobModel job = findJob(jobId);
        TaskModel task = findTaskInJob(job, taskId);

        if (!task.getStatus().equals(TaskModel.TaskStatus.ASSIGNED)) {
            throw new IllegalStateException("Task must be ASSIGNED to start");
        }
        if (!task.getPickedBy().equals(employeeId)) {
            throw new IllegalStateException("Only assigned employee can start task");
        }

        task.setStatus(TaskModel.TaskStatus.IN_PROGRESS);
        task.setStartedAt(LocalDateTime.now());

        jobRepository.save(job);
        return new JobResponse(job);
    }

    /* -------------------------------------------------------------------------- */

    /* ----------------- COMPLETING TASK BY THE STARTED EMPLOYEE ---------------- */

    

    /* -------------------------------------------------------------------------- */

    /* -------------------------------------------------------------------------- */
    /* ----------------------------- UTILITY METHODS ---------------------------- */
    /* -------------------------------------------------------------------------- */

    /* ------------------------------ TASK CREATOR ------------------------------ */

    private TaskModel taskCreator(TaskRequest taskRequest) {
        TaskModel taskModel = new TaskModel();
        taskModel.setName(taskRequest.getName());
        taskModel.setDesignation(taskRequest.getDesignation());
        taskModel.setTechniqueType(TaskModel.TechniqueType
                .valueOf(taskRequest.getTechniqueType().toUpperCase()));
        for (LocationDetailsRequest location_detail_request : taskRequest.getLocationsDetails()) {
            taskModel.addNewLocationDetail(locationDetailsCreator(location_detail_request));
        }
        return taskModel;
    }

    /* -------------------------------------------------------------------------- */

    /* ---------------------------- LOCATION CREATOR ---------------------------- */

    private LocationDetailsModel locationDetailsCreator(LocationDetailsRequest request) {
        LocationDetailsModel locationDetailsModel = new LocationDetailsModel();
        locationDetailsModel.setLocations(request.getLocations());
        for (FlimRequest flimRequest : request.getFlims()) {
            locationDetailsModel.addNewFlim(flimCreator(flimRequest));
        }
        return locationDetailsModel;
    }

    /* -------------------------------------------------------------------------- */

    /* ------------------------------ FLIM CREATOR ------------------------------ */

    private FlimModel flimCreator(FlimRequest flimRequest) {
        FlimModel flim = new FlimModel(flimRequest.getLength(),
                flimRequest.getBreadth(),
                flimRequest.getSourceName(),
                flimRequest.getFilmType(),
                flimRequest.getFlimThickness());
        return flim;
    }

    /* -------------------------------------------------------------------------- */

    /* ------------------------- PICK JOB FROM DATA BASE ------------------------ */

    private JobModel findJob(String jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job not found : " +
                        jobId));
    }

    /* -------------------------------------------------------------------------- */

    /* ------------------------- PICK TASK FROM THE JOB ------------------------- */

    private TaskModel findTaskInJob(JobModel job, String taskId) {
        return job.getTasks().stream()
                .filter(t -> t.getId().equals(taskId))
                .findFirst().orElseThrow(() -> new TaskNotFoundException("Task not found with ID: " + taskId));
    }

    /* -------------------------------------------------------------------------- */

}

// // --- TASK LIFECYCLE METHODS ---

// public JobResponse completeTask(String jobId, String taskId, String
// employeeId) {
// JobModel job = findJob(jobId);
// TaskModel task = findTaskInJob(job, taskId);

// if (!task.getStatus().equals(TaskModel.TaskStatus.IN_PROGRESS)) {
// throw new IllegalStateException("Task must be IN_PROGRESS to complete");
// }
// if (!task.getPickedBy().equals(employeeId)) {
// throw new IllegalStateException("Only assigned employee can complete task");
// }

// task.setStatus(TaskModel.TaskStatus.COMPLETED);
// task.setCompletedAt(LocalDateTime.now());

// // Recalculate job metrics (completedTasks changes)
// recalculateJobMetrics(job);
// JobModel saved = jobRepository.save(job);
// return new JobResponse(saved);
// }

// // --- JOB METRICS RECALCULATION ---
// public void recalculateJobMetrics(JobModel job) {
// 1. Recalculate source usage
// Map<String, Integer> sourceAreaMap = new HashMap<>();
// for (TaskModel task : job.getTasks()) {
// if (task.getFlim() != null &&
// task.getFlim().getSourceName() != null &&
// task.getFlim().getLength() != null &&
// task.getFlim().getBreadth() != null) {

// String sourceName = task.getFlim().getSourceName();
// double area = task.getFlim().getLength() * task.getFlim().getBreadth() *
// task.getLocations().size() * task.getFlim().getFilmType();
// int roundedArea = (int) Math.ceil(area);
// sourceAreaMap.merge(sourceName, roundedArea, Integer::sum);
// }
// }

// job.setSourceUsedInSquareInch(
// sourceAreaMap.entrySet().stream()
// .map(e -> new SourceInJob(e.getKey(), e.getValue()))
// .collect(Collectors.toList()));

// // 2. Update totals
// job.setTotalTasks(job.getTasks().size());
// job.setTotalSquareInch(
// job.getSourceUsedInSquareInch().stream()
// .mapToInt(SourceInJob::getTotalSqareInch)
// .sum());

// // 3. Update completed tasks and status
// long completed = job.getTasks().stream()
// .filter(t -> t.getStatus() == TaskModel.TaskStatus.COMPLETED)
// .count();
// job.setCompletedTasks((int) completed);
// job.updateStatus();
// }

// // --- UTILITY METHODS ---

// private TaskModel createTaskFromRequest(TaskRequest tr) {
// TaskModel task = new TaskModel();
// task.setId(UUID.randomUUID().toString());
// task.setName(tr.getName());
// task.setDesignation(tr.getDesignation());
// task.setLocations(tr.getLocations());
// task.setTechniqueType(TaskModel.TechniqueType.valueOf(tr.getTechniqueType().toUpperCase()));
// TaskModel.Flim flim = new TaskModel.Flim();
// flim.setLength(tr.getFlim().getLength());
// flim.setBreadth(tr.getFlim().getBreadth());
// flim.setSourceName(tr.getFlim().getSourceName());
// flim.setFilmType(tr.getFlim().getFilmType());
// task.setFlim(flim);
// task.setStatus(TaskModel.TaskStatus.PENDING);
// return task;
// }

// private <T> List<T> safeList(List<T> list) {
// return list != null ? list : new ArrayList<>();
// }

// public List<JobResponse> getJobsByOrganization(String organizationName) {
// return jobRepository.findByOrganizationName(organizationName).stream()
// .map(JobResponse::new)
// .toList();
// }

// public List<JobResponse> getJobsByManager(String createdBy) {
// return jobRepository.findByCreatedBy(createdBy).stream()
// .map(JobResponse::new)
// .toList();
// }

// public JobPageResponse getJobsCreatedInLastWeek(int page, int size, String
// sortBy) {
// LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);

// // Default sort field
// String sortField = (sortBy == null || sortBy.trim().isEmpty()) ? "createdAt"
// : sortBy;

// // Validate sort field to prevent injection (only allow safe fields)
// if (!"createdAt".equals(sortField)) {
// sortField = "createdAt"; // fallback
// }

// // Create Pageable object
// PageRequest pageable = PageRequest.of(
// page, // page number (0-indexed)
// size, // page size
// Sort.by(sortField).descending() // sort by createdAt DESC
// );

// Page<JobModel> jobPage =
// jobRepository.findByCreatedAtAfterOrderByCreatedAtDesc(oneWeekAgo, pageable);

// return new JobPageResponse(jobPage);
// }

// public List<JobSummaryResponse> getAllJobsSummary() {
// return jobRepository.findAll().stream()
// .map(JobSummaryResponse::new)
// .toList();
// }

// public void deleteJobById(String jobId, String currentUserId) {
// // Optional: Check if job exists (for 404 if needed)
// JobModel job = jobRepository.findById(jobId)
// .orElseThrow(() -> new JobNotFoundException("Job not found"));

// Optional: Prevent deletion of COMPLETED or IN_PROGRESS jobs
// JobModel job = jobRepository.findById(jobId).orElseThrow(...);
// if (job.getStatus() != JobStatus.PENDING) {
// throw new IllegalStateException("Cannot delete non-PENDING job");
// }
// Only creator or admin can delete
// if (!job.getCreatedBy().equals(currentUserId) && !isAdmin(currentUserId)) {
// throw new AccessDeniedException("You can only delete your own jobs");
// }

// jobRepository.deleteById(jobId);
// }
// }

// public JobPageResponse getJobsCreatedInLastWeek(int page, int size, String
// sortBy) {

// // Default sort field
// sortBy = (sortBy == null || sortBy.trim().isEmpty()) ? "createdAt"
// : sortBy;

// // Validate sort field to prevent injection (only allow safe fields)
// if (!"createdAt".equals(sortBy)) {
// sortBy = "createdAt"; // fallback
// }

// // Create Pageable object
// PageRequest pageable = PageRequest.of(
// page, // page number (0-indexed)
// size, // page size
// Sort.by(sortBy).ascending() // sort by createdAt DESC
// );
// // Pageable pageable = PageRequest.of(page, size,
// Sort.by(Sort.Direction.DESC,
// // sortBy));
// Page<JobModel> jobPage = jobRepository.findAll(pageable);
// return new JobPageResponse(jobPage);
// }
