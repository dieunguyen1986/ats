package com.ats.job.dto;

import com.ats.job.entity.JobPostingStatus;

import java.time.LocalDateTime;

/**
 * Public-facing DTO for a single Job Posting displayed on the Career Site.
 *
 * <p>This record is intentionally lightweight — it exposes only candidate-relevant
 * information. Internal fields (createdBy, updatedBy, deletedAt, recruiterId) are NEVER included.</p>
 *
 * <p>Field contract per AC1:</p>
 * <ul>
 *   <li>{@code id}              – unique identifier for deep-link: /career-site/jobs/{id}</li>
 *   <li>{@code title}           – Job title shown on the card</li>
 *   <li>{@code departmentName}  – Department name for filtering (resolved from FK)</li>
 *   <li>{@code location}        – Work location (city or "Remote")</li>
 *   <li>{@code status}          – Always PUBLISHED for Career Site responses</li>
 *   <li>{@code publishedAt}     – Displayed as "Posted Date" on the listing card</li>
 * </ul>
 */
public record JobPostingResponse(
        Long id,
        String title,
        String departmentName,
        String location,
        JobPostingStatus status,
        LocalDateTime publishedAt
) {
}
