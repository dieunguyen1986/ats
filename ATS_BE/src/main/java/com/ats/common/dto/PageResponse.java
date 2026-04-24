package com.ats.common.dto;

import org.springframework.data.domain.Page;
import java.util.List;

/**
 * Standardized pagination response wrapper.
 * Maps Spring Data JPA Page object to the expected JSON format.
 *
 * <p>Usage example:</p>
 * <pre>
 * Page&lt;Candidate&gt; entityPage = repository.findAll(pageable);
 * PageResponse&lt;Candidate&gt; response = PageResponse.from(entityPage);
 * return BaseResponse.success(response);
 * </pre>
 *
 * @param <T> the type of the elements in the page content
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {
    public static <T> PageResponse<T> from(Page<T> springPage) {
        return new PageResponse<>(
                springPage.getContent(),
                springPage.getNumber(),
                springPage.getSize(),
                springPage.getTotalElements(),
                springPage.getTotalPages(),
                springPage.isLast()
        );
    }
}
