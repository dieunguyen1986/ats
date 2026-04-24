import { Outlet } from 'react-router';

/**
 * Public layout for the Career Site pages (job listing, job detail, application form).
 * No authentication required. Simple header with company branding.
 */
export const CareerLayout = () => {
  return (
    <div className="flex min-h-screen flex-col bg-background">
      {/* Career Site Header */}
      <header className="sticky top-0 z-40 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
        <div className="container mx-auto flex h-16 items-center justify-between px-4">
          <div className="flex items-center gap-3">
            <span className="text-xl font-bold tracking-tight">
              ATS Careers
            </span>
          </div>
          <nav className="flex items-center gap-4 text-sm">
            <a
              href="/"
              className="text-muted-foreground hover:text-foreground transition-colors"
            >
              Staff Login
            </a>
          </nav>
        </div>
      </header>

      {/* Career Site Content */}
      <main className="flex-1">
        <Outlet />
      </main>

      {/* Career Site Footer */}
      <footer className="border-t py-6">
        <div className="container mx-auto px-4 text-center text-sm text-muted-foreground">
          © {new Date().getFullYear()} ATS — Applicant Tracking System. All rights reserved.
        </div>
      </footer>
    </div>
  );
};
