package com.ats.job.repository;

import com.ats.job.entity.JobPosting;
import com.ats.job.entity.JobPostingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link JobPosting} entities.
 *
 * <p>All queries automatically exclude soft-deleted records due to the
 * {@code @SQLRestriction("is_deleted = false")} annotation on the entity.</p>
 */
@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    /**
     * [AC1] Retrieve all published job postings, sorted by most-recent first.
     *
     * @param status   the target status (always {@link JobPostingStatus#PUBLISHED} for Career Site)
     * @param pageable pagination and sorting config
     * @return a page of published job postings
     */
    Page<JobPosting> findAllByStatus(JobPostingStatus status, Pageable pageable);

    /**
     * [AC2] Retrieve published job postings filtered by keyword (searches title OR department,
     * case-insensitive) using JPQL LOWER() for database-agnostic case folding.
     *
     * @param status     the target status (always {@link JobPostingStatus#PUBLISHED})
     * @param keyword    the search keyword (lowercase expected from service layer)
     * @param pageable   pagination and sorting config
     * @return a page of matching published jobs
     */
    @Query("""
            SELECT j FROM JobPosting j
            WHERE j.status = :status
              AND (LOWER(j.title) LIKE LOWER(:keyword)
                OR LOWER(j.department.departmentName) LIKE LOWER(:keyword))
            """)
    Page<JobPosting> findAllByStatusAndKeyword(
            @Param("status") JobPostingStatus status,
            @Param("keyword") String keyword,
            Pageable pageable);
}
