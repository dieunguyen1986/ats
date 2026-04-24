package com.ats.job.entity;

import com.ats.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import com.ats.common.entity.Department;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a Job Requisition / Posting managed by Recruiters.
 * Maps to the physical DB table: jobs
 *
 * <p>Only records with {@link JobPostingStatus#PUBLISHED} status are visible
 * on the public Career Site. Hibernate's @SQLRestriction ensures soft-deleted
 * records are always excluded from queries.</p>
 *
 * <p>This entity belongs to the {@code job} bounded context.
 * Cross-module access must go through {@code JobPostingService}.</p>
 */
@Getter
@Setter
@Entity
@Table(name = "jobs")
@SQLRestriction("is_deleted = false")
public class JobPosting extends BaseEntity {

    /**
     * The department this job belongs to. FK → departments.id
     * NOT NULL in DB: every job must be linked to a department.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    /**
     * The recruiter who owns this job requisition. FK → users.id
     * Stored as Long to avoid cross-module coupling (no User entity reference).
     */
    @Column(name = "recruiter_id", nullable = false)
    private Long recruiterId;

    /**
     * Job title displayed on the Career Site (e.g., "Backend Engineer").
     * DB: VARCHAR(500)
     */
    @Column(name = "title", nullable = false, length = 500)
    private String title;

    /**
     * Full job description. DB: TEXT
     */
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    /**
     * Work location or "Remote". DB: VARCHAR(500)
     */
    @Column(name = "location", length = 500)
    private String location;

    /**
     * Minimum salary offered. DB: NUMERIC(15,2)
     */
    @Column(name = "salary_min", precision = 15, scale = 2)
    private BigDecimal salaryMin;

    /**
     * Maximum salary offered. DB: NUMERIC(15,2)
     */
    @Column(name = "salary_max", precision = 15, scale = 2)
    private BigDecimal salaryMax;

    /**
     * Lifecycle status controlling Career Site visibility.
     * DB: VARCHAR(50) — Enum: DRAFT, PUBLISHED, CLOSED
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private JobPostingStatus status = JobPostingStatus.DRAFT;

    /**
     * UTM tracking source for analytics.
     */
    @Column(name = "utm_source", length = 150)
    private String utmSource;

    /**
     * UTM tracking medium for analytics.
     */
    @Column(name = "utm_medium", length = 150)
    private String utmMedium;

    /**
     * Application deadline. After this date, the posting should be closed.
     */
    @Column(name = "deadline")
    private LocalDateTime deadline;

    /**
     * Timestamp when the posting was moved to PUBLISHED state.
     * Null while still in DRAFT.
     */
    @Column(name = "published_at")
    private LocalDateTime publishedAt;
}
