package com.ats.job.controller;

import com.ats.common.dto.PageResponse;
import com.ats.job.dto.JobPostingFilterRequest;
import com.ats.job.dto.JobPostingResponse;
import com.ats.job.entity.EmploymentType;
import com.ats.job.entity.JobPostingStatus;
import com.ats.job.service.JobPostingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for CareerSiteController → JobPostingService interaction.
 *
 * <p><b>Test Level:</b> Integration Test (Controller ↔ Service layer)</p>
 * <p><b>Feature:</b> Job Management — Get Published Jobs (ATP-101)</p>
 * <p><b>Technique:</b> @WebMvcTest with MockBean for service layer</p>
 *
 * <p>Maps to ITC-JOB-004-xx test cases in the IT report.</p>
 *
 * @see CareerSiteController
 * @see JobPostingService
 */
@WebMvcTest(CareerSiteController.class)
@DisplayName("[IT] CareerSiteController → JobPostingService")
class CareerSiteControllerIntegrationTest {

    private static final String BASE_URL = "/api/v1/career-site/jobs";

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private JobPostingService jobPostingService;

    // ================================================================
    // Helper: create sample JobPostingResponse
    // ================================================================
    private JobPostingResponse sampleJob(String title, String department) {
        return null;
//                new JobPostingResponse(
//                randomUUID(),
//                title,
//                department,
//                "Ho Chi Minh City",
//                EmploymentType.FULL_TIME,
//                JobPostingStatus.PUBLISHED,
//                LocalDateTime.of(2026, 4, 20, 10, 0)
//        );
    }

    private PageResponse<JobPostingResponse> pageOf(List<JobPostingResponse> items, int totalElements) {
        return new PageResponse<>(
                items,
                0,
                20,
                totalElements,
                (int) Math.ceil((double) totalElements / 20),
                true
        );
    }

    // ================================================================
    // ITC-JOB-004-01: Listing returns only PUBLISHED jobs with pagination
    // ================================================================
    @Nested
    @DisplayName("ITC-JOB-004-01: Get published jobs with pagination")
    class GetPublishedJobsWithPagination {

        /**
         * ITC-JOB-004-01
         * Verify listing returns only PUBLISHED jobs with pagination
         * via Controller → Service integration.
         *
         * <p>Procedure:</p>
         * <ol>
         *   <li>Call GET /api/v1/career-site/jobs?page=0&size=10 via MockMvc</li>
         *   <li>Controller delegates to JobPostingService.getPublishedJobs(filter)</li>
         *   <li>Service returns paginated PUBLISHED jobs sorted by createdAt DESC</li>
         * </ol>
         *
         * <p>Expected: Response 200 OK with paginated content,
         * all items have status PUBLISHED.</p>
         *
         * <p>Pre-conditions: Multiple jobs exist with mixed statuses.
         * Endpoint is public (no auth required).</p>
         */
        @Test
        @DisplayName("should return 200 OK with paginated PUBLISHED jobs")
        void should_return200WithPaginatedPublishedJobs_when_validRequest() throws Exception {
            // Arrange
            List<JobPostingResponse> jobs = List.of(
                    sampleJob("Java Developer", "Engineering"),
                    sampleJob("HR Specialist", "Human Resources"),
                    sampleJob("Data Analyst", "Analytics")
            );
            PageResponse<JobPostingResponse> page = pageOf(jobs, 3);

            when(jobPostingService.getPublishedJobs(any(JobPostingFilterRequest.class)))
                    .thenReturn(page);

            // Act & Assert
            mockMvc.perform(get(BASE_URL)
                            .param("page", "0")
                            .param("size", "10"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("Job listings retrieved successfully"))
                    .andExpect(jsonPath("$.data.content", hasSize(3)))
                    .andExpect(jsonPath("$.data.content[0].title").value("Java Developer"))
                    .andExpect(jsonPath("$.data.content[0].status").value("PUBLISHED"))
                    .andExpect(jsonPath("$.data.content[1].status").value("PUBLISHED"))
                    .andExpect(jsonPath("$.data.content[2].status").value("PUBLISHED"))
                    .andExpect(jsonPath("$.data.totalElements").value(3))
                    .andExpect(jsonPath("$.data.totalPages").value(1))
                    .andExpect(jsonPath("$.data.page").value(0))
                    .andExpect(jsonPath("$.data.last").value(true));

            // Verify Controller→Service delegation
            verify(jobPostingService, times(1)).getPublishedJobs(any(JobPostingFilterRequest.class));
        }

        @Test
        @DisplayName("should pass correct filter params from Controller to Service")
        void should_passCorrectFilterParams_when_requestHasCustomPagination() throws Exception {
            // Arrange
            PageResponse<JobPostingResponse> emptyPage = pageOf(Collections.emptyList(), 0);
            when(jobPostingService.getPublishedJobs(any(JobPostingFilterRequest.class)))
                    .thenReturn(emptyPage);

            // Act
            mockMvc.perform(get(BASE_URL)
                            .param("page", "2")
                            .param("size", "5"))
                    .andExpect(status().isOk());

            // Assert — verify the filter passed to service has correct page/size
            verify(jobPostingService).getPublishedJobs(argThat(filter ->
                    filter.page() == 2 && filter.size() == 5 && !filter.hasKeyword()
            ));
        }
    }

    // ================================================================
    // ITC-JOB-004-02: Keyword search filters job listings
    // ================================================================
    @Nested
    @DisplayName("ITC-JOB-004-02: Keyword search")
    class KeywordSearchFilter {

        /**
         * ITC-JOB-004-02
         * Verify keyword search filters job listings correctly.
         *
         * <p>Procedure:</p>
         * <ol>
         *   <li>Call GET /api/v1/career-site/jobs?keyword=Java&page=0&size=10</li>
         *   <li>Controller delegates to Service with keyword filter</li>
         *   <li>Service builds query with LIKE '%Java%' on title/description</li>
         * </ol>
         *
         * <p>Expected: All returned jobs contain "Java" in title or description.
         * Response time &lt; 2 seconds.</p>
         */
        @Test
        @DisplayName("should return only jobs matching keyword in title or department")
        void should_returnFilteredJobs_when_keywordProvided() throws Exception {
            // Arrange
            List<JobPostingResponse> filteredJobs = List.of(
                    sampleJob("Java Developer", "Engineering"),
                    sampleJob("Senior Java Architect", "Engineering")
            );
            PageResponse<JobPostingResponse> page = pageOf(filteredJobs, 2);

            when(jobPostingService.getPublishedJobs(any(JobPostingFilterRequest.class)))
                    .thenReturn(page);

            // Act & Assert
            mockMvc.perform(get(BASE_URL)
                            .param("keyword", "Java")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.content", hasSize(2)))
                    .andExpect(jsonPath("$.data.content[0].title", containsString("Java")))
                    .andExpect(jsonPath("$.data.content[1].title", containsString("Java")))
                    .andExpect(jsonPath("$.data.totalElements").value(2));

            // Verify keyword was passed correctly to service
            verify(jobPostingService).getPublishedJobs(argThat(filter ->
                    "Java".equals(filter.keyword()) && filter.hasKeyword()
            ));
        }
    }

    // ================================================================
    // ITC-JOB-004-03: Empty result when no published jobs match filter
    // ================================================================
    @Nested
    @DisplayName("ITC-JOB-004-03: Empty result set")
    class EmptyResultSet {

        /**
         * ITC-JOB-004-03
         * Verify empty result when no published jobs match filter.
         *
         * <p>Procedure:</p>
         * <ol>
         *   <li>Call GET /api/v1/career-site/jobs?keyword=xyzNotExist</li>
         *   <li>Controller delegates to Service</li>
         *   <li>Service queries Repository, finds no matches</li>
         * </ol>
         *
         * <p>Expected: Response 200 OK with empty content list,
         * totalElements = 0, totalPages = 0.</p>
         */
        @Test
        @DisplayName("should return 200 OK with empty content when no jobs match keyword")
        void should_returnEmptyContent_when_noJobsMatchKeyword() throws Exception {
            // Arrange
            PageResponse<JobPostingResponse> emptyPage = new PageResponse<>(
                    Collections.emptyList(), 0, 20, 0, 0, true
            );
            when(jobPostingService.getPublishedJobs(any(JobPostingFilterRequest.class)))
                    .thenReturn(emptyPage);

            // Act & Assert
            mockMvc.perform(get(BASE_URL)
                            .param("keyword", "xyzNotExist"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.content", hasSize(0)))
                    .andExpect(jsonPath("$.data.content", empty()))
                    .andExpect(jsonPath("$.data.totalElements").value(0))
                    .andExpect(jsonPath("$.data.totalPages").value(0));
        }

        @Test
        @DisplayName("should return 200 OK with empty content when no published jobs exist at all")
        void should_returnEmptyContent_when_noPublishedJobsExist() throws Exception {
            // Arrange
            PageResponse<JobPostingResponse> emptyPage = new PageResponse<>(
                    Collections.emptyList(), 0, 20, 0, 0, true
            );
            when(jobPostingService.getPublishedJobs(any(JobPostingFilterRequest.class)))
                    .thenReturn(emptyPage);

            // Act & Assert
            mockMvc.perform(get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.content", empty()))
                    .andExpect(jsonPath("$.data.totalElements").value(0));
        }
    }

    // ================================================================
    // Additional edge cases for robustness
    // ================================================================
    @Nested
    @DisplayName("Edge Cases: Default parameters & content-type")
    class EdgeCases {

        @Test
        @DisplayName("should use default page=0 and size=20 when not specified")
        void should_useDefaults_when_noParamsProvided() throws Exception {
            // Arrange
            PageResponse<JobPostingResponse> emptyPage = pageOf(Collections.emptyList(), 0);
            when(jobPostingService.getPublishedJobs(any(JobPostingFilterRequest.class)))
                    .thenReturn(emptyPage);

            // Act
            mockMvc.perform(get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"));

            // Verify defaults
            verify(jobPostingService).getPublishedJobs(argThat(filter ->
                    filter.page() == 0 && filter.size() == 20 && !filter.hasKeyword()
            ));
        }

        @Test
        @DisplayName("should return application/json content type")
        void should_returnJsonContentType() throws Exception {
            // Arrange
            PageResponse<JobPostingResponse> page = pageOf(
                    List.of(sampleJob("Test Job", "QA")), 1
            );
            when(jobPostingService.getPublishedJobs(any(JobPostingFilterRequest.class)))
                    .thenReturn(page);

            // Act & Assert
            mockMvc.perform(get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.data.content[0].id").isNotEmpty())
                    .andExpect(jsonPath("$.data.content[0].title").value("Test Job"))
                    .andExpect(jsonPath("$.data.content[0].department").value("QA"))
                    .andExpect(jsonPath("$.data.content[0].location").value("Ho Chi Minh City"))
                    .andExpect(jsonPath("$.data.content[0].employmentType").value("FULL_TIME"))
                    .andExpect(jsonPath("$.data.content[0].publishedAt").isNotEmpty());
        }
    }
}
