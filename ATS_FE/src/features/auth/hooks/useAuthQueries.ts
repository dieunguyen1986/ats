import { useState, useCallback } from 'react';
import { authService } from '../services/auth.service';
import type { UserProfileResponse } from '../types/auth.types';


/**
 * Custom hooks for authentication queries (fetching current user, etc.)
 */
export const useAuthQueries = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [data, setData] = useState<UserProfileResponse | null>(null);
  const [error, setError] = useState<Error | null>(null);

  // Remove unused logout extraction

  const fetchCurrentUser = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const response = await authService.getCurrentUser() as any;
      setData(response);
      return response;
    } catch (err: any) {
      setError(err);
      // If unauthorized, the API interceptor will dispatch 'auth:logout',
      // which is caught by useAuth to clear the store.
      throw err;
    } finally {
      setIsLoading(false);
    }
  }, []);

  return {
    fetchCurrentUser,
    data,
    isLoading,
    error,
  };
};
