import { useState } from 'react';
import { Outlet, NavLink } from 'react-router';
import { useAuth } from '@/shared/hooks/useAuth';
import { cn } from '@/shared/utils/cn.util';
import {
  LayoutDashboard,
  Briefcase,
  Users,
  Kanban,
  CalendarDays,
  BarChart3,
  LogOut,
  PanelLeftClose,
  PanelLeft,
} from 'lucide-react';

const NAV_ITEMS = [
  { to: '/dashboard', label: 'Dashboard', icon: LayoutDashboard },
  { to: '/jobs', label: 'Jobs', icon: Briefcase },
  { to: '/candidates', label: 'Candidates', icon: Users },
  { to: '/pipeline', label: 'Pipeline', icon: Kanban },
  { to: '/interviews', label: 'Interviews', icon: CalendarDays },
  { to: '/analytics', label: 'Analytics', icon: BarChart3 },
];

/**
 * Main application layout with topbar, collapsible sidebar, and content area.
 * Used for all authenticated/internal pages.
 */
export const MainLayout = () => {
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);
  const { user, logout } = useAuth();

  return (
    <div className="flex min-h-screen flex-col bg-background">
      {/* ─── Topbar ─────────────────────────────────────────────────────── */}
      <header className="sticky top-0 z-40 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
        <div className="flex h-16 items-center justify-between px-4">
          <div className="flex items-center gap-4">
            {/* Sidebar Toggle */}
            <button
              onClick={() => setSidebarCollapsed((prev) => !prev)}
              className="hidden rounded-md p-2 text-muted-foreground hover:bg-accent hover:text-accent-foreground md:inline-flex"
              aria-label={sidebarCollapsed ? 'Expand sidebar' : 'Collapse sidebar'}
            >
              {sidebarCollapsed ? (
                <PanelLeft className="h-5 w-5" />
              ) : (
                <PanelLeftClose className="h-5 w-5" />
              )}
            </button>

            <span className="text-lg font-bold tracking-tight">ATS System</span>
          </div>

          {/* User Actions */}
          <div className="flex items-center gap-3">
            <span className="hidden text-sm text-muted-foreground sm:inline-block">
              {user?.fullName || 'User'}
            </span>
            <div className="flex h-8 w-8 items-center justify-center rounded-full bg-primary text-xs font-semibold text-primary-foreground">
              {user?.fullName?.charAt(0)?.toUpperCase() || 'U'}
            </div>
            <button
              onClick={logout}
              className="rounded-md p-2 text-muted-foreground hover:bg-accent hover:text-accent-foreground transition-colors"
              aria-label="Logout"
            >
              <LogOut className="h-4 w-4" />
            </button>
          </div>
        </div>
      </header>

      <div className="flex flex-1">
        {/* ─── Sidebar ────────────────────────────────────────────────── */}
        <aside
          className={cn(
            'hidden flex-col border-r bg-background transition-all duration-200 md:flex',
            sidebarCollapsed ? 'w-[68px]' : 'w-[220px]',
          )}
        >
          <nav className="flex flex-1 flex-col gap-1 px-3 py-4">
            {NAV_ITEMS.map(({ to, label, icon: Icon }) => (
              <NavLink
                key={to}
                to={to}
                className={({ isActive }) =>
                  cn(
                    'flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium transition-colors',
                    isActive
                      ? 'bg-accent text-accent-foreground'
                      : 'text-muted-foreground hover:bg-accent/50 hover:text-accent-foreground',
                  )
                }
                title={sidebarCollapsed ? label : undefined}
              >
                <Icon className="h-5 w-5 shrink-0" />
                {!sidebarCollapsed && <span>{label}</span>}
              </NavLink>
            ))}
          </nav>
        </aside>

        {/* ─── Main Content ───────────────────────────────────────────── */}
        <main className="flex-1 overflow-auto p-6">
          <Outlet />
        </main>
      </div>
    </div>
  );
};
