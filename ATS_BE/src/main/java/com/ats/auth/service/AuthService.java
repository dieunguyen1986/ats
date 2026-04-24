package com.ats.auth.service;

import com.ats.auth.dto.LoginRequest;
import com.ats.auth.dto.LoginResponse;
import com.ats.auth.dto.RefreshTokenRequest;
import com.ats.auth.dto.SsoLoginRequest;
import com.ats.auth.dto.UserProfileResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    LoginResponse ssoLogin(SsoLoginRequest request);
    LoginResponse refreshToken(RefreshTokenRequest request);
    void logout(String token);
    UserProfileResponse getCurrentUser();
}
