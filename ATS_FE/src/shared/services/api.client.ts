import axios from 'axios';
import type { ApiErrorResponse } from '@/shared/types/api.types';
import { ApiError } from '@/shared/services/api.error';

/**
 * Centralized Axios instance for all API calls.
 * Configured with base URL, timeout, and interceptors per L3 conventions.
 *
 * - Request interceptor: auto-attaches JWT Bearer token from localStorage.
 * - Response interceptor: unwraps ApiResponse<T> → returns only the `data` payload.
 * - Error interceptor: transforms backend errors into typed ApiError instances.
 * - 401 handler: dispatches 'auth:logout' event for global logout.
 */
const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL + '/api/v1',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// ─── Request Interceptor ────────────────────────────────────────────────────
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error),
);

// ─── Response Interceptor ───────────────────────────────────────────────────
apiClient.interceptors.response.use(
  (response) => {
    // Unwrap ApiResponse<T> envelope — return only the `data` field (actual payload).
    // Backend always returns { code, message, data } structure per L2.
    return response.data?.data !== undefined ? response.data.data : response.data;
  },
  (error) => {
    // Handle 401 Unauthorized globally — trigger logout event
    if (error.response?.status === 401) {
      localStorage.removeItem('accessToken');
      window.dispatchEvent(new CustomEvent('auth:logout'));
      return Promise.reject(
        new ApiError(401, 'Session expired. Please login again.', [], error),
      );
    }

    // Parse backend error response body into typed ApiError
    if (error.response?.data) {
      const errorBody = error.response.data as ApiErrorResponse;
      return Promise.reject(
        new ApiError(
          errorBody.code || error.response.status,
          errorBody.message || 'An unexpected error occurred',
          errorBody.errors || [],
          error,
        ),
      );
    }

    // Network error or timeout (no response body)
    if (error.code === 'ECONNABORTED') {
      return Promise.reject(
        new ApiError(0, 'Request timed out. Please try again.', [], error),
      );
    }

    return Promise.reject(
      new ApiError(0, 'Network error. Please check your connection.', [], error),
    );
  },
);

export { apiClient };
