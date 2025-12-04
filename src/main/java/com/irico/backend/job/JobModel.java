package com.irico.backend.job;

import com.irico.backend.source.SourceModel;
import com.irico.backend.task.TaskModel;
import com.irico.backend.task.TaskModel.TaskStatus;
import com.irico.backend.task.dto.FlimRequest;
import com.irico.backend.task.dto.LocationDetailsRequest;
import com.irico.backend.task.exception.TaskNotFoundException;

import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "job")
@Data
public class JobModel {

	public JobModel() {
		this.status = JobStatus.PENDING;
		this.totalTasks = 0;
		this.completedTasks = 0;
		this.totalSquareInch = new BigDecimal(0);
		this.totalRatePerJob = new BigDecimal(0);
		this.tasks = new ArrayList<>();
		this.sourceUsedInSquareInch = new ArrayList<>();
	}

	public enum JobStatus {
		PENDING, IN_PROGRESS, COMPLETED
	}

	@Id
	private String id;

	@Field("job_name")
	// * name of the job
	private String name;

	@Field("media_url")
	// * media for the job
	private String mediaUrl;

	@Field("organization_name")
	// * job belongs to organization name
	private String organizationName;

	@Field("created_by")
	// * which manager's id created the job
	private String createdBy;

	@Field("creator_name")
	// * which manager's name in user db created the job
	private String createdByName;

	@CreatedDate
	@Field("created_at")
	// * the timestamp which job created
	private Instant createdAt;

	@Field("total_square_inch_per_job")
	// * the total sqare inch of the job where repect to the source used
	private BigDecimal totalSquareInch;

	@Field("total_rate_per_job")
	// * the total rate of the job depends on the square inch of source in ₹(rupees)
	private BigDecimal totalRatePerJob;

	@Field("total_tasks")
	// * total tasks belongs to this job
	private Integer totalTasks;

	@Field("completed_tasks")
	// * the count of the tasks which have the status completed
	private Integer completedTasks;

	@Field("status")
	// * depends on the task completed conut the job status will reflect
	private JobStatus status;

	@Field("source_used_sq_inch")
	// * list of source used in the job per task
	private List<SourceModel> sourceUsedInSquareInch;

	/* -------------------------------------------------------------------------- */
	/* -------------- UTILITY METHODS FOR SOURCE USED IN SQARE INCH ------------- */
	/* -------------------------------------------------------------------------- */

	public Boolean addSourceUsedInSquareInch(SourceModel source) {
		return this.sourceUsedInSquareInch.add(source);
	}

	/* -------------------------------------------------------------------------- */

	public Boolean removeSourceUsedInSquareInch(String taskId) {
		return this.sourceUsedInSquareInch.removeIf(source -> source.getSourceId().equals(taskId));
	}

	/* -------------------------------------------------------------------------- */

	@Field("tasks")
	// * list of tasks belongs to the job
	private List<TaskModel> tasks;

	/* -------------------------------------------------------------------------- */
	/* ------------------ UTILITY METHODS FOR TASKS OF THE JOB ------------------ */
	/* -------------------------------------------------------------------------- */

	/* --------------------------- ADD TASK TO THE JOB -------------------------- */

	public Boolean addTask(TaskModel task) {
		this.totalTasks += 1;
		return this.tasks.add(task);
	}

	/* -------------------------------------------------------------------------- */

	public TaskModel removeTask(int index, String taskId) {
		if (index == -1) {
			throw new TaskNotFoundException("Task is not found id : " + taskId);
		}
		return this.tasks.remove(index);
	}

	/* -------------------------------------------------------------------------- */

	public Boolean removeTask(String taskId) {
		TaskModel task = removeTask(getTask(taskId), taskId);
		Boolean is_task_removed = true;
		Boolean is_source_usage_removed = removeSourceUsedInSquareInch(task.getId());
		return is_task_removed && is_source_usage_removed;
	}

	/* -------------------------------------------------------------------------- */

	private int getTask(String taskId) {
		for (int i = 0; i < this.tasks.size(); i++) {
			if (tasks.get(i).getId().equals(taskId))
				return i;
		}
		return -1;
	}

	/* -------------------------------------------------------------------------- */

	public void updateStatus() {
		if (completedTasks == null)
			completedTasks = 0;
		if (totalTasks == null)
			totalTasks = 0;

		if (totalTasks == 0) {
			this.status = JobStatus.PENDING;
		} else if (completedTasks == 0) {
			this.status = JobStatus.PENDING;
		} else if (completedTasks < totalTasks) {
			this.status = JobStatus.IN_PROGRESS;
		} else if (completedTasks >= totalTasks) {
			this.status = JobStatus.COMPLETED;
		}
	}

	/* -------------------------------------------------------------------------- */

	private BigDecimal[] calculateSourceCountWithRate(LocationDetailsRequest request, FlimRequest flim) {
		int location_size = request.getLocations().size();
		BigDecimal length = flim.getLength();
		BigDecimal breadth = flim.getBreadth();
		int flim_type = flim.getFilmType();

		BigDecimal area = breadth.multiply(length);
		BigDecimal total_square_inch_per_task_per_flim = BigDecimal.valueOf(flim_type)
				.multiply(BigDecimal.valueOf(location_size)).multiply(area);
		BigDecimal total_square_inch_per_task_per_flim_rate_per_sqare_inch = flim.getSourceRatePerSquareInch()
				.multiply(total_square_inch_per_task_per_flim);
		return new BigDecimal[] { total_square_inch_per_task_per_flim,
				total_square_inch_per_task_per_flim_rate_per_sqare_inch };
	}

	/* -------------------------------------------------------------------------- */

	private void sourceCalculator(LocationDetailsRequest request, String taskId) {
		for (FlimRequest flim : request.getFlims()) {
			SourceModel sourceModel = new SourceModel(taskId);
			sourceModel.setSourceName(flim.getSourceName());
			BigDecimal source_count_and_source_rate[] = calculateSourceCountWithRate(request, flim);
			sourceModel.setSourceRate(source_count_and_source_rate[1]);
			sourceModel.setSourceCount(source_count_and_source_rate[0]);
			this.addSourceUsedInSquareInch(sourceModel);
		}
	}

	/* -------------------------------------------------------------------------- */

	public void calculateSource(LocationDetailsRequest request, String taskId) {
		sourceCalculator(request, taskId);
	}

	/* -------------------------------------------------------------------------- */

	public void calculate() {
		calculateMetrics();
	}

	/* -------------------------------------------------------------------------- */

	private void calculateMetrics() {
		BigDecimal totalRatePerJob = new BigDecimal(0);
		BigDecimal totalSquareInch = new BigDecimal(0);
		for (TaskModel task : this.tasks) {
			for (SourceModel source : sourceUsedInSquareInch) {
				if (task.getId().equals(source.getSourceId()) && TaskStatus.COMPLETED == task.getStatus()) {
					totalSquareInch = totalSquareInch.add(source.getSourceCount());
					totalRatePerJob = totalRatePerJob.add(source.getSourceRate());
				}
			}
		}

		this.totalRatePerJob = totalRatePerJob;
		this.totalSquareInch = totalSquareInch;
	}

	/* -------------------------------------------------------------------------- */
}