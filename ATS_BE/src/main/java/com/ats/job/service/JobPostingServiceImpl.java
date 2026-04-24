package com.ats.job.service;

import com.ats.common.dto.PageResponse;
import com.ats.job.dto.JobPostingFilterRequest;
import com.ats.job.dto.JobPostingMapper;
import com.ats.job.dto.JobPostingResponse;
import com.ats.job.entity.JobPosting;
import com.ats.job.entity.JobPostingStatus;
import com.ats.job.repository.JobPostingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Business logic implementation for the Career Site — View Job Listing feature.
 *
 * <p><b>Acceptance Criteria satisfied:</b></p>
 * <ul>
 *   <li>AC1: Queries only {@link JobPostingStatus#PUBLISHED} records; sorted by
 *       {@code publishedAt DESC} (most-recent first).</li>
 *   <li>AC2: When a {@code keyword} is provided, the query is narrowed to records whose
 *       {@code title} OR {@code department} contains the keyword (case-insensitive LIKE).</li>
 *   <li>AC3: An empty page is returned gracefully — no exception is thrown. The controller
 *       and client are responsible for rendering the "No jobs available" empty state.</li>
 * </ul>
 *
 * <p><b>Architecture rules followed:</b></p>
 * <ul>
 *   <li>All queries are {@code readOnly = true} — no unnecessary write-locks.</li>
 *   <li>Entity ↔ DTO mapping is delegated to {@link JobPostingMapper} — never done inline.</li>
 *   <li>This class does NOT access any repository from another bounded context.</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobPostingServiceImpl implements JobPostingService {

    private final JobPostingRepository jobPostingRepository;
    private final JobPostingMapper jobPostingMapper;

    /**
     * Returns a paginated list of PUBLISHED job postings, with optional keyword filtering.
     *
     * <p>Sorting: {@code publishedAt DESC} ensures most-recently-published jobs appear first
     * (AC1). Falls back to {@code createdAt DESC} if {@code publishedAt} is null (edge-case
     * for seeded/migrated data).</p>
     *
     * @param filter the filter and pagination parameters from the request
     * @return a {@link PageResponse} containing mapped {@link JobPostingResponse} DTOs
     */
    @Override
    @Transactional(readOnly = true)
    public PageResponse<JobPostingResponse> getPublishedJobs(JobPostingFilterRequest filter) {
        log.info("Career Site: Fetching published jobs | keyword='{}', page={}, size={}",
                filter.keyword(), filter.page(), filter.size());

        // AC1 / AC2 — construct pageable with most-recent-first sort
        Pageable pageable = PageRequest.of(
                filter.page(),
                filter.size(),
                Sort.by(Sort.Direction.DESC, "publishedAt", "createdAt")
        );

        // Route to keyword or plain query based on presence of filter term
        Page<JobPostingResponse> responsePage;

        if (filter.hasKeyword()) {
            // AC2: narrow by keyword across title + department
            log.debug("Career Site: Applying keyword filter '{}'", filter.keyword());
            String searchPattern = "%" + filter.keyword() + "%";
            Page<JobPosting> entityPage = jobPostingRepository.findAllByStatusAndKeyword(
                    JobPostingStatus.PUBLISHED,
                    searchPattern,
                    pageable
            );
            responsePage = entityPage.map(jobPostingMapper::toResponse);
        } else {
            // AC1: no filter — return all published jobs
            Page<JobPosting> entityPage = jobPostingRepository.findAllByStatus(
                    JobPostingStatus.PUBLISHED,
                    pageable
            );
            responsePage = entityPage.map(jobPostingMapper::toResponse);
        }

        // AC3: empty page is valid — no exception, just an empty content list
        log.info("Career Site: Returning {} jobs (total: {})",
                responsePage.getNumberOfElements(), responsePage.getTotalElements());

        return PageResponse.from(responsePage);
    }
}
