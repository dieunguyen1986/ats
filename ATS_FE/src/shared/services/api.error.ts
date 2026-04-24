import type { FieldError } from '@/shared/types/api.types';

/**
 * Typed API error class that wraps backend error responses.
 * Provides structured access to error code, message, and field-level validation errors.
 */
export class ApiError extends Error {
  public readonly code: number;
  public readonly errors: FieldError[];
  public readonly originalError: unknown;

  constructor(
    code: number,
    message: string,
    errors: FieldError[] = [],
    originalError?: unknown,
  ) {
    super(message);
    this.name = 'ApiError';
    this.code = code;
    this.errors = errors;
    this.originalError = originalError;
  }

  /**
   * Check if the error contains field-level validation errors.
   */
  hasFieldErrors(): boolean {
    return this.errors.length > 0;
  }

  /**
   * Get the error message for a specific field.
   */
  getFieldError(field: string): string | undefined {
    return this.errors.find((e) => e.field === field)?.message;
  }
}

/**
 * Type guard to check if an unknown error is an ApiError instance.
 */
export function isApiError(error: unknown): error is ApiError {
  return error instanceof ApiError;
}
