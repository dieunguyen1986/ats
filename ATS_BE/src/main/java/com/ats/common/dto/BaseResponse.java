package com.ats.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Universal API response wrapper.
 * Provides a standardized JSON structure for success and error responses.
 *
 * <p>Usage examples:</p>
 * <pre>
 * // Success Response
 * return BaseResponse.success(candidateResponse);
 * 
 * // Created Response
 * return BaseResponse.created(candidateResponse);
 *
 * // Error Response without details
 * return BaseResponse.error(404, "Candidate not found");
 * 
 * // Error Response with validation details
 * return BaseResponse.error(400, "Validation failed", validationErrors);
 * </pre>
 *
 * @param <T> the type of the payload data
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record BaseResponse<T>(
        int code,
        String message,
        T data,
        Object errors
) {

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(200, "Success", data, null);
    }

    public static <T> BaseResponse<T> success(String message, T data) {
        return new BaseResponse<>(200, message, data, null);
    }

    public static <T> BaseResponse<T> created(T data) {
        return new BaseResponse<>(201, "Created", data, null);
    }

    public static <T> BaseResponse<T> noContent() {
        return new BaseResponse<>(204, "No Content", null, null);
    }

    public static <T> BaseResponse<T> error(int code, String message) {
        return new BaseResponse<>(code, message, null, null);
    }

    public static <T> BaseResponse<T> error(int code, String message, Object errors) {
        return new BaseResponse<>(code, message, null, errors);
    }
}
