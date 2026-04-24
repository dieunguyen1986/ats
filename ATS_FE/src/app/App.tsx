import { AppRouter } from '@/app/routers/AppRouter';
import { ToastProvider } from '@/shared/components/ToastProvider';

export function App() {
  return (
    <>
      <AppRouter />
      <ToastProvider />
    </>
  );
}
