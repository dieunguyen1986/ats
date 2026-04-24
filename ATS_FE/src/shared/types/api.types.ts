/**
 * Standard success response wrapper from Backend API (L2 convention).
 * Every API response is wrapped in this structure.
 */
export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

/**
 * Paginated list response structure (L2 convention).
 * Used for all list/search endpoints.
 */
export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

/**
 * Field-level validation error detail (L2 convention).
 * Returned in 400/422 error responses.
 */
export interface FieldError {
  field: string;
  message: string;
}

/**
 * Error response structure from Backend API (L2 convention).
 * Used for all error responses (400, 401, 403, 404, 409, 422, 500).
 */
export interface ApiErrorResponse {
  code: number;
  message: string;
  errors?: FieldError[];
}

/**
 * Base entity fields present on all persisted domain objects.
 */
export interface BaseEntity {
  id: number;
  createdAt: string;
  updatedAt: string;
}

/**
 * Pagination query parameters for list endpoints.
 */
export interface PaginationParams {
  page?: number;
  size?: number;
  sort?: string;
}

/**
 * Sort direction for query parameters.
 */
export type SortDirection = 'asc' | 'desc';
