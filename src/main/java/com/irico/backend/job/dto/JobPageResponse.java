package com.irico.backend.job.dto;

import lombok.Data;
import org.springframework.data.domain.Page;
import com.irico.backend.job.JobModel;

import java.util.List;

@Data
public class JobPageResponse {
    private List<JobResponse> content; // The actual jobs
    private int number; // Current page (0-indexed)
    private int size; // Items per page
    private long totalElements; // Total matching jobs
    private int totalPages; // Total pages
    private boolean first; // Is this the first page?
    private boolean last; // Is this the last page?

    public JobPageResponse(Page<JobModel> jobPage) {
        this.content = jobPage.getContent().stream()
                .map(JobResponse::new)
                .toList();
        this.number = jobPage.getNumber();
        this.size = jobPage.getSize();
        this.totalElements = jobPage.getTotalElements();
        this.totalPages = jobPage.getTotalPages();
        this.first = jobPage.isFirst();
        this.last = jobPage.isLast();
    }
}