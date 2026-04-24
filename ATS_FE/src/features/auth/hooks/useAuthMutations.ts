import { useState, useCallback } from 'react';
import { authService } from '../services/auth.service';
import type { LoginRequest, SsoLoginRequest } from '../types/auth.types';
import { useAuth } from '@/shared/hooks/useAuth';
import { useToast } from '@/shared/hooks/useToast';

/**
 * Custom hooks for authentication mutations (login, logout, etc.)
 * Handles loading states, error reporting, and Zustand auth state updates.
 */
export const useAuthMutations = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);
  
  const { login: setAuthLogin, logout: setAuthLogout } = useAuth();
  const { success, error: toastError } = useToast();

  const login = useCallback(async (data: LoginRequest) => {
    setIsLoading(true);
    setError(null);
    try {
      // The API client interceptor unwraps the data, so response is LoginResponse directly
      const response = await authService.login(data) as any;
      setAuthLogin(response.accessToken, {
        email: response.email,
        fullName: response.fullName,
        roles: response.roles,
      });
      success('Login successful', 'Welcome back to ATS System.');
      return response;
    } catch (err: any) {
      setError(err);
      toastError('Login failed', err?.message || 'Invalid email or password.');
      throw err;
    } finally {
      setIsLoading(false);
    }
  }, [setAuthLogin, success, toastError]);

  const ssoLogin = useCallback(async (data: SsoLoginRequest) => {
    setIsLoading(true);
    setError(null);
    try {
      const response = await authService.ssoLogin(data) as any;
      setAuthLogin(response.accessToken, {
        email: response.email,
        fullName: response.fullName,
        roles: response.roles,
      });
      success('SSO Login successful', 'Welcome back to ATS System.');
      return response;
    } catch (err: any) {
      setError(err);
      toastError('SSO Login failed', err?.message || 'Authentication error.');
      throw err;
    } finally {
      setIsLoading(false);
    }
  }, [setAuthLogin, success, toastError]);

  const logout = useCallback(async () => {
    setIsLoading(true);
    try {
      await authService.logout();
    } catch (err) {
      // Ignore errors on logout (e.g. token already expired)
      console.warn('Logout API error:', err);
    } finally {
      setIsLoading(false);
      setAuthLogout();
      // Optional: success('Logged out', 'You have been successfully logged out.');
    }
  }, [setAuthLogout]);

  return {
    login,
    ssoLogin,
    logout,
    isLoading,
    error,
  };
};
