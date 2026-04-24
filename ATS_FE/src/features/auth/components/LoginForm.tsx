import { useState } from 'react';
import { Button } from '@/shared/components/Button';
import { InputField } from '@/shared/components/InputField';
import { isApiError } from '@/shared/services/api.error';
import { Eye, EyeOff } from 'lucide-react';

interface LoginFormProps {
  onSubmit: (email: string, password: string) => Promise<void>;
  isLoading: boolean;
  error: Error | null;
}

export const LoginForm = ({ onSubmit, isLoading, error }: LoginFormProps) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);

  // Extract field-level errors if it's an ApiError
  const apiError = isApiError(error) ? error : null;
  const emailError = apiError?.getFieldError('email');
  const passwordError = apiError?.getFieldError('password');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!email || !password) return;
    await onSubmit(email, password);
  };

  return (
    <form onSubmit={handleSubmit} className="flex flex-col gap-5 w-full">
      <InputField
        id="email"
        type="email"
        label="Email Address"
        placeholder="name@company.com"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        error={emailError}
        disabled={isLoading}
        required
        autoFocus
        autoComplete="email"
      />

      <div className="relative">
        <InputField
          id="password"
          type={showPassword ? 'text' : 'password'}
          label="Password"
          placeholder="••••••••"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          error={passwordError}
          disabled={isLoading}
          required
          autoComplete="current-password"
        />
        <button
          type="button"
          onClick={() => setShowPassword(!showPassword)}
          className="absolute right-3 top-8 text-muted-foreground hover:text-foreground transition-colors"
          tabIndex={-1}
          aria-label={showPassword ? "Hide password" : "Show password"}
        >
          {showPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
        </button>
        <div className="absolute right-0 top-0">
          <a href="#" className="text-xs font-medium text-primary hover:underline" tabIndex={-1}>
            Forgot password?
          </a>
        </div>
      </div>

      <Button
        type="submit"
        className="w-full mt-2"
        size="lg"
        isLoading={isLoading}
        disabled={!email || !password}
      >
        Sign In
      </Button>
    </form>
  );
};
