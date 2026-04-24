package com.ats.job.controller;

import com.ats.common.dto.BaseResponse;
import com.ats.common.dto.PageResponse;
import com.ats.job.dto.JobPostingFilterRequest;
import com.ats.job.dto.JobPostingResponse;
import com.ats.job.service.JobPostingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for the public Career Site — Job Board.
 *
 * <p><b>Authorization:</b> All endpoints are <em>public</em> (no JWT required).
 * This is declared as {@code permitAll()} in {@code SecurityConfig}.</p>
 *
 * <p><b>Controller responsibilities:</b></p>
 * <ul>
 *   <li>Map HTTP request params to {@link JobPostingFilterRequest}.</li>
 *   <li>Delegate processing entirely to {@link JobPostingService}.</li>
 *   <li>Wrap service result in {@link BaseResponse} before returning.</li>
 *   <li>NEVER contain any business logic.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/career-site")
@RequiredArgsConstructor
@Tag(name = "Career Site", description = "Public job board for candidates — no authentication required")
public class CareerSiteController {

    private final JobPostingService jobPostingService;

    /**
     * [AC1 / AC2 / AC3] Get paginated list of published job postings.
     *
     * <p>Supports optional free-text keyword search across job title and department.</p>
     *
     * <pre>
     * GET /api/v1/career-site/jobs
     * GET /api/v1/career-site/jobs?keyword=java
     * GET /api/v1/career-site/jobs?keyword=engineering&amp;page=0&amp;size=20
     * </pre>
     *
     * <p>Response shape:</p>
     * <pre>
     * {
     *   "code": 200,
     *   "message": "Job listings retrieved successfully",
     *   "data": {
     *     "content": [ { ... }, { ... } ],
     *     "page": 0,
     *     "size": 20,
     *     "totalElements": 42,
     *     "totalPages": 3,
     *     "last": false
     *   }
     * }
     * </pre>
     *
     * @param keyword    optional search keyword — matches job title or department (case-insensitive)
     * @param page       zero-indexed page number (default: 0)
     * @param size       page size between 1 and 100 (default: 20)
     * @return {@code 200 OK} with paginated job listings (empty list when no matches — AC3)
     */
    @GetMapping("/jobs")
    @Operation(
            summary = "List published job postings",
            description = "Returns a paginated list of all PUBLISHED job postings on the Career Site. "
                    + "Optionally filter by keyword (matches title or department). No authentication required."
    )
    public ResponseEntity<BaseResponse<PageResponse<JobPostingResponse>>> getPublishedJobs(
            @Parameter(description = "Free-text keyword to filter by title or department")
            @RequestParam(required = false) String keyword,

            @Parameter(description = "Zero-indexed page number (default: 0)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Items per page — max 100 (default: 20)")
            @RequestParam(defaultValue = "20") int size
    ) {
        JobPostingFilterRequest filter = new JobPostingFilterRequest(keyword, page, size);

        PageResponse<JobPostingResponse> data = jobPostingService.getPublishedJobs(filter);

        return ResponseEntity.ok(
                BaseResponse.success("Job listings retrieved successfully", data)
        );
    }
}
