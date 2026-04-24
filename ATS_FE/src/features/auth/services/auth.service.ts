import { apiClient } from '@/shared/services/api.client';
import type { ApiResponse } from '@/shared/types/api.types';
import type {
  LoginRequest,
  SsoLoginRequest,
  RefreshTokenRequest,
  LoginResponse,
  UserProfileResponse,
} from '../types/auth.types';

/**
 * Authentication & Authorization API service
 */
export const authService = {
  /**
   * User login with credentials
   */
  login: async (data: LoginRequest) => {
    return apiClient.post<ApiResponse<LoginResponse>>('/auth/login', data);
  },

  /**
   * SSO/LDAP federated login callback
   */
  ssoLogin: async (data: SsoLoginRequest) => {
    return apiClient.post<ApiResponse<LoginResponse>>('/auth/login/sso', data);
  },

  /**
   * Refresh expired JWT access token
   */
  refreshToken: async (data: RefreshTokenRequest) => {
    return apiClient.post<ApiResponse<LoginResponse>>('/auth/refresh', data);
  },

  /**
   * Invalidate current JWT token session
   */
  logout: async () => {
    return apiClient.post<ApiResponse<null>>('/auth/logout');
  },

  /**
   * Get current authenticated user profile
   */
  getCurrentUser: async () => {
    return apiClient.get<ApiResponse<UserProfileResponse>>('/auth/me');
  },
};
