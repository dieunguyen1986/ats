import { useNavigate, useLocation } from 'react-router';
import { useAuthMutations } from '../hooks/useAuthMutations';
import { LoginForm } from '../components/LoginForm';
import { SsoButtons } from '../components/SsoButtons';

/**
 * Smart Component for the Login Page.
 * Combines LoginForm and SsoButtons, hooks up mutations, and handles routing.
 */
const LoginPage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { login, ssoLogin, isLoading, error } = useAuthMutations();

  // Redirect to the intended page after login, or default to dashboard
  const from = location.state?.from?.pathname || '/dashboard';

  const handleEmailLogin = async (email: string, password: string) => {
    try {
      await login({ email, password });
      navigate(from, { replace: true });
    } catch (err) {
      // Error is caught and displayed by the hook and LoginForm
    }
  };

  const handleSsoLogin = async (provider: 'GOOGLE' | 'AZURE_AD' | 'LDAP') => {
    try {
      // In a real flow, this would redirect to provider, but we simulate the callback
      // using a dummy token since we are just mocking the UI.
      const fakeToken = `dummy-token-from-${provider.toLowerCase()}`;
      await ssoLogin({ ssoToken: fakeToken, provider });
      navigate(from, { replace: true });
    } catch (err) {
      // Error is caught and displayed by the hook
    }
  };

  return (
    <div className="flex flex-col space-y-6 sm:w-[400px] w-full px-4 sm:px-0 mx-auto">
      <div className="flex flex-col space-y-2 text-center mb-4">
        <div className="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-2xl bg-primary shadow-lg shadow-primary/20">
          <span className="text-3xl font-extrabold text-primary-foreground tracking-tight">T</span>
        </div>
        <h1 className="text-3xl font-bold tracking-tight text-foreground">
          TechCorp ATS
        </h1>
        <p className="text-sm text-muted-foreground font-medium">
          Internal Portal for Recruiters & Managers
        </p>
      </div>

      <div className="rounded-xl border bg-card text-card-foreground shadow-sm p-6 sm:p-8">
        <LoginForm
          onSubmit={handleEmailLogin}
          isLoading={isLoading}
          error={error}
        />
        <SsoButtons
          onSsoClick={handleSsoLogin}
          isLoading={isLoading}
        />
      </div>

      <div className="text-center text-xs text-muted-foreground font-medium">
        SCR-WEB-031 / UC-00.1 — Authentication Gateway
      </div>
    </div>
  );
};

export default LoginPage;
