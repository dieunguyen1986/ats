import { Suspense, lazy, useEffect } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router';
import { MainLayout } from '@/app/layouts/MainLayout';
import { AuthLayout } from '@/app/layouts/AuthLayout';
import { CareerLayout } from '@/app/layouts/CareerLayout';
import { PrivateRoute } from '@/app/guards/PrivateRoute';
import { useAuth } from '@/shared/hooks/useAuth';
import { Spinner } from '@/shared/components/Spinner';

// ─── Lazy-loaded Page Components ─────────────────────────────────────────────
// Each feature page is lazy-loaded for optimal bundle splitting.

// Auth pages
const LoginPage = lazy(() => import('@/features/auth/pages/LoginPage'));

// Internal pages (protected)
const DashboardPage = lazy(() => import('@/features/analytics/pages/DashboardPage'));
const JobListPage = lazy(() => import('@/features/job/pages/JobListPage'));
const CandidateListPage = lazy(() => import('@/features/candidate/pages/CandidateListPage'));
const PipelinePage = lazy(() => import('@/features/pipeline/pages/PipelinePage'));
const InterviewListPage = lazy(() => import('@/features/interview/pages/InterviewListPage'));
const AnalyticsPage = lazy(() => import('@/features/analytics/pages/AnalyticsPage'));

// Public career site pages
const CareerJobListPage = lazy(() => import('@/features/career-site/pages/CareerJobListPage'));

/**
 * Page-level loading fallback component.
 */
const PageLoader = () => (
  <div className="flex h-[50vh] items-center justify-center">
    <Spinner size="lg" />
  </div>
);

/**
 * Root application router.
 * Defines all routes with layouts, guards, and lazy-loaded pages.
 *
 * Route structure:
 * - /auth/*         → AuthLayout (public)
 * - /careers/*      → CareerLayout (public)
 * - /*              → MainLayout + PrivateRoute (protected)
 */
export const AppRouter = () => {
  const hydrate = useAuth((state) => state.hydrate);

  // Hydrate auth state from localStorage on mount
  useEffect(() => {
    hydrate();
  }, [hydrate]);

  return (
    <BrowserRouter>
      <Suspense fallback={<PageLoader />}>
        <Routes>
          {/* ── Public Routes: Auth ───────────────────────────────────── */}
          <Route path="/auth" element={<AuthLayout />}>
            <Route path="login" element={<LoginPage />} />
            <Route index element={<Navigate to="login" replace />} />
          </Route>

          {/* ── Public Routes: Career Site ────────────────────────────── */}
          <Route path="/careers" element={<CareerLayout />}>
            <Route index element={<CareerJobListPage />} />
          </Route>

          {/* ── Protected Routes: Internal App ────────────────────────── */}
          <Route element={<PrivateRoute />}>
            <Route path="/" element={<MainLayout />}>
              <Route index element={<Navigate to="dashboard" replace />} />
              <Route path="dashboard" element={<DashboardPage />} />
              <Route path="jobs" element={<JobListPage />} />
              <Route path="candidates" element={<CandidateListPage />} />
              <Route path="pipeline" element={<PipelinePage />} />
              <Route path="interviews" element={<InterviewListPage />} />
              <Route path="analytics" element={<AnalyticsPage />} />
            </Route>
          </Route>

          {/* ── Fallback 404 ──────────────────────────────────────────── */}
          <Route
            path="*"
            element={
              <div className="flex h-screen items-center justify-center">
                <div className="text-center">
                  <h1 className="text-4xl font-bold text-foreground">404</h1>
                  <p className="mt-2 text-muted-foreground">Page not found</p>
                </div>
              </div>
            }
          />
        </Routes>
      </Suspense>
    </BrowserRouter>
  );
};
