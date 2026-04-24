package com.ats.job.entity;

/**
 * Lifecycle status of a Job Posting.
 *
 * <ul>
 *   <li>{@code DRAFT}     – Being composed by HR; hidden from Career Site.</li>
 *   <li>{@code PUBLISHED} – Visible to the public on the Career Site.</li>
 *   <li>{@code CLOSED}    – Position filled or cancelled; hidden from Career Site.</li>
 * </ul>
 */
public enum JobPostingStatus {
    DRAFT,
    PUBLISHED,
    CLOSED
}
