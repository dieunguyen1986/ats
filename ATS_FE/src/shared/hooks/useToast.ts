import { create } from 'zustand';

export type ToastType = 'success' | 'error' | 'warning' | 'info';

export interface Toast {
  id: string;
  type: ToastType;
  title: string;
  description?: string;
}

interface ToastState {
  toasts: Toast[];
  addToast: (toast: Omit<Toast, 'id'>) => void;
  removeToast: (id: string) => void;
  success: (title: string, description?: string) => void;
  error: (title: string, description?: string) => void;
  warning: (title: string, description?: string) => void;
  info: (title: string, description?: string) => void;
}

let toastCounter = 0;
const AUTO_DISMISS_MS = 5000;

/**
 * Zustand store for toast notification management.
 * Provides typed helper methods (success, error, warning, info) and auto-dismiss.
 */
export const useToast = create<ToastState>((set) => ({
  toasts: [],

  addToast: (toast) => {
    const id = `toast-${++toastCounter}-${Date.now()}`;
    set((state) => ({
      toasts: [...state.toasts, { ...toast, id }],
    }));

    // Auto-dismiss after timeout
    setTimeout(() => {
      set((state) => ({
        toasts: state.toasts.filter((t) => t.id !== id),
      }));
    }, AUTO_DISMISS_MS);
  },

  removeToast: (id) =>
    set((state) => ({
      toasts: state.toasts.filter((t) => t.id !== id),
    })),

  success: (title, description) =>
    useToast.getState().addToast({ type: 'success', title, description }),

  error: (title, description) =>
    useToast.getState().addToast({ type: 'error', title, description }),

  warning: (title, description) =>
    useToast.getState().addToast({ type: 'warning', title, description }),

  info: (title, description) =>
    useToast.getState().addToast({ type: 'info', title, description }),
}));
