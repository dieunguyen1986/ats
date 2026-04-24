package com.ats.job.service;

import com.ats.common.dto.PageResponse;
import com.ats.job.dto.JobPostingFilterRequest;
import com.ats.job.dto.JobPostingResponse;

/**
 * Service contract for the Career Site — Job Listing feature (ATP-101).
 *
 * <p>All implementations must satisfy the three Acceptance Criteria:</p>
 * <ul>
 *   <li>AC1 — Return all PUBLISHED jobs sorted by most-recent date.</li>
 *   <li>AC2 — Return filtered results when a keyword is supplied (title OR department).</li>
 *   <li>AC3 — Return an empty page (not an error) when no postings match.</li>
 * </ul>
 */
public interface JobPostingService {

    /**
     * Retrieves a paginated list of publicly visible job postings.
     *
     * @param filter encapsulates optional keyword and pagination params
     * @return a page of {@link JobPostingResponse} DTOs; never {@code null}
     */
    PageResponse<JobPostingResponse> getPublishedJobs(JobPostingFilterRequest filter);
}
