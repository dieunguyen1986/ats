import { useToast, type ToastType } from '@/shared/hooks/useToast';
import { cn } from '@/shared/utils/cn.util';
import { X } from 'lucide-react';

const iconMap: Record<ToastType, string> = {
  success: '✓',
  error: '✕',
  warning: '⚠',
  info: 'ℹ',
};

const styleMap: Record<ToastType, string> = {
  success: 'border-l-emerald-500 bg-emerald-50 text-emerald-900',
  error: 'border-l-red-500 bg-red-50 text-red-900',
  warning: 'border-l-amber-500 bg-amber-50 text-amber-900',
  info: 'border-l-blue-500 bg-blue-50 text-blue-900',
};

const iconStyleMap: Record<ToastType, string> = {
  success: 'bg-emerald-500',
  error: 'bg-red-500',
  warning: 'bg-amber-500',
  info: 'bg-blue-500',
};

/**
 * Global toast notification provider.
 * Renders active toasts from the Zustand toast store with animations and auto-dismiss.
 * Must be mounted once at the App root level.
 */
export const ToastProvider = () => {
  const { toasts, removeToast } = useToast();

  if (toasts.length === 0) return null;

  return (
    <div className="fixed bottom-4 right-4 z-50 flex max-h-screen w-full flex-col gap-2 p-4 sm:bottom-4 sm:right-4 md:max-w-[420px]">
      {toasts.map((toast) => (
        <div
          key={toast.id}
          className={cn(
            'flex items-start gap-3 rounded-lg border-l-4 p-4 shadow-lg transition-all duration-300 animate-in slide-in-from-right-full',
            styleMap[toast.type],
          )}
          role="alert"
        >
          {/* Icon */}
          <span
            className={cn(
              'flex h-6 w-6 shrink-0 items-center justify-center rounded-full text-xs font-bold text-white',
              iconStyleMap[toast.type],
            )}
          >
            {iconMap[toast.type]}
          </span>

          {/* Content */}
          <div className="flex-1 min-w-0">
            <p className="text-sm font-semibold">{toast.title}</p>
            {toast.description && (
              <p className="mt-1 text-xs opacity-80">{toast.description}</p>
            )}
          </div>

          {/* Close button */}
          <button
            onClick={() => removeToast(toast.id)}
            className="shrink-0 rounded-md p-1 opacity-60 hover:opacity-100 transition-opacity"
            aria-label="Dismiss notification"
          >
            <X className="h-4 w-4" />
          </button>
        </div>
      ))}
    </div>
  );
};
