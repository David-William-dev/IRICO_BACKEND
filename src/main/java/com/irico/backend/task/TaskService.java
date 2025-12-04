package com.irico.backend.task;

import com.irico.backend.job.JobService;
import com.irico.backend.job.dto.JobResponse;
import com.irico.backend.task.dto.TaskRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskService {
    @Autowired
    private JobService jobService;

    /* -------------------------------------------------------------------------- */
    /* ------------------------------ TASK SERVICES ----------------------------- */
    /* -------------------------------------------------------------------------- */

    /* --------------------- ADD NEW TASK TO A EXISTING JOB --------------------- */

    public JobResponse addTaskToJob(String jobId, TaskRequest taskRequest) {
        return jobService.addTaskToJob(jobId, taskRequest);
    }

    /* -------------------------------------------------------------------------- */

    /* ----------------- DELETE EXISTING TASK FROM EXISTING JOB ----------------- */

    public JobResponse deleteTaskFromJob(String jobId, String taskId) {
        return jobService.deleteTaskFromJob(jobId, taskId);
    }

    /* -------------------------------------------------------------------------- */

    /* --------- UPDATE EXISTING TASK IN THE JOB WITH JOB ID AND TASK ID -------- */

    public JobResponse updateTaskInJob(String jobId, String taskId, TaskRequest taskRequest) {
        return jobService.updateTaskInJob(jobId, taskId, taskRequest);
    }

    /* -------------------------------------------------------------------------- */

    /* ------------------------ ASSIGN TASK TO A EMPLOYEE ----------------------- */

    public JobResponse assignTask(String jobId, String taskId, String assignedToId, String assignedToName) {
        return jobService.assignTask(jobId, taskId, assignedToId, assignedToName);
    }

    /* -------------------------------------------------------------------------- */

    /* --------------------- START TASK BY ASSIGNED EMPLOYEE -------------------- */

    public JobResponse startTask(String jobId, String taskId, String employeeId) {
        return jobService.startTask(jobId, taskId, employeeId);
    }

    /* -------------------------------------------------------------------------- */
}

// public JobResponse completeTask(String jobId, String taskId, String
// employeeId) {
// return jobService.completeTask(jobId, taskId, employeeId);
// }
