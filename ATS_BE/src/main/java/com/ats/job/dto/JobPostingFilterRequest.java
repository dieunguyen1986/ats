package com.ats.job.dto;

/**
 * Query parameters for filtering the Career Site job listing.
 *
 * <p>All fields are optional. If {@code keyword} is blank, all PUBLISHED
 * postings are returned (AC1). When non-blank, results are narrowed to
 * postings whose title or department contains the keyword (AC2).</p>
 */
public record JobPostingFilterRequest(

        /**
         * Optional free-text keyword — searched in job title AND department.
         * Case-insensitive. Example: "java", "engineering".
         */
        String keyword,

        /**
         * Zero-indexed page number (default: 0).
         */
        int page,

        /**
         * Number of items per page (default: 20, max: 100).
         */
        int size
) {
    /**
     * Compact canonical constructor applying default values.
     */
    public JobPostingFilterRequest {
        if (page < 0) page = 0;
        if (size <= 0) size = 20;
        if (size > 100) size = 100;
        if (keyword != null) keyword = keyword.strip();
    }

    /**
     * Returns {@code true} if a non-blank keyword was provided (AC2 path).
     */
    public boolean hasKeyword() {
        return keyword != null && !keyword.isBlank();
    }
}
