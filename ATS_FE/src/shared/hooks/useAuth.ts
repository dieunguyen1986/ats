import { create } from 'zustand';

/**
 * Basic user info stored in auth state after login.
 */
export interface AuthUser {
  email: string;
  fullName: string;
  roles: string[];
}

interface AuthState {
  token: string | null;
  user: AuthUser | null;
  isAuthenticated: boolean;

  /** Set auth token and user info after successful login. */
  login: (token: string, user: AuthUser) => void;

  /** Clear auth state and redirect to login. */
  logout: () => void;

  /** Set token only (e.g., from localStorage hydration). */
  setToken: (token: string) => void;

  /** Hydrate auth state from localStorage on app init. */
  hydrate: () => void;
}

/**
 * Zustand store for authentication state management.
 * Provides centralized auth state instead of scattered localStorage reads.
 *
 * Usage:
 *   const { isAuthenticated, user, login, logout } = useAuth();
 */
export const useAuth = create<AuthState>((set) => ({
  token: null,
  user: null,
  isAuthenticated: false,

  login: (token, user) => {
    localStorage.setItem('accessToken', token);
    localStorage.setItem('authUser', JSON.stringify(user));
    set({ token, user, isAuthenticated: true });
  },

  logout: () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('authUser');
    set({ token: null, user: null, isAuthenticated: false });
  },

  setToken: (token) => {
    localStorage.setItem('accessToken', token);
    set({ token, isAuthenticated: true });
  },

  hydrate: () => {
    const token = localStorage.getItem('accessToken');
    const userJson = localStorage.getItem('authUser');

    if (token) {
      let user: AuthUser | null = null;
      if (userJson) {
        try {
          user = JSON.parse(userJson) as AuthUser;
        } catch {
          // Corrupted user data — clear it
          localStorage.removeItem('authUser');
        }
      }
      set({ token, user, isAuthenticated: true });
    }
  },
}));

// ─── Global Logout Listener ──────────────────────────────────────────────────
// Listen for 'auth:logout' events dispatched by the API client (on 401).
if (typeof window !== 'undefined') {
  window.addEventListener('auth:logout', () => {
    useAuth.getState().logout();
  });
}
