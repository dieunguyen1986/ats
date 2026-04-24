export interface LoginRequest {
  email: string;
  password?: string;
}

export interface SsoLoginRequest {
  ssoToken: string;
  provider: 'LDAP' | 'GOOGLE' | 'AZURE_AD';
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
  tokenType: string;
  email: string;
  fullName: string;
  roles: string[];
}

export interface UserProfileResponse {
  id: number;
  email: string;
  fullName: string;
  roles: string[];
  status: 'ACTIVE' | 'INACTIVE';
  lastLoginAt: string;
}
