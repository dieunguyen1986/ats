import { Navigate, Outlet, useLocation } from 'react-router';
import { useAuth } from '@/shared/hooks/useAuth';

/**
 * Auth guard component for protected routes.
 * Redirects unauthenticated users to login page while preserving the attempted URL.
 * Uses centralized Zustand auth store instead of direct localStorage access.
 */
export const PrivateRoute = () => {
  const location = useLocation();
  const isAuthenticated = useAuth((state) => state.isAuthenticated);

  if (!isAuthenticated) {
    return <Navigate to="/auth/login" state={{ from: location }} replace />;
  }

  return <Outlet />;
};
