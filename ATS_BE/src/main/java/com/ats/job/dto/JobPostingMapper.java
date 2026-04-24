package com.ats.job.dto;

import com.ats.job.entity.JobPosting;
import org.springframework.stereotype.Component;

/**
 * Manual mapper between the {@link JobPosting} entity and its DTOs.
 *
 * <p>Mapping is done manually (no MapStruct) to keep dependencies minimal and
 * stay explicit about which fields are exposed. This class MUST NOT be bypassed —
 * controllers must never touch entity objects directly.</p>
 */
@Component
public class JobPostingMapper {

    /**
     * Converts a {@link JobPosting} entity to the public-facing {@link JobPostingResponse} DTO.
     *
     * <p>Only candidate-safe fields are included. Internal audit fields
     * (createdBy, updatedBy, deletedAt) are deliberately excluded.</p>
     *
     * @param entity the managed JPA entity
     * @return a lightweight Career Site response DTO
     */
    public JobPostingResponse toResponse(JobPosting entity) {
        return new JobPostingResponse(
                entity.getId(),
                entity.getTitle(),
                entity.getDepartment() != null ? entity.getDepartment().getDepartmentName() : null,
                entity.getLocation(),
                entity.getStatus(),
                entity.getPublishedAt()
        );
    }
}
