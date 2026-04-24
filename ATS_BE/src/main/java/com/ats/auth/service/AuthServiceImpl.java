package com.ats.auth.service;

import com.ats.auth.dto.LoginRequest;
import com.ats.auth.dto.LoginResponse;
import com.ats.auth.dto.RefreshTokenRequest;
import com.ats.auth.dto.SsoLoginRequest;
import com.ats.auth.dto.UserProfileResponse;
import com.ats.auth.entity.User;
import com.ats.auth.repository.SsoConfigurationRepository;
import com.ats.auth.repository.UserRepository;
import com.ats.common.exception.ResourceNotFoundException;
import com.ats.config.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final SsoConfigurationRepository ssoConfigurationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("Processing login for user: {}", request.email());
        
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        return buildLoginResponse(user);
    }

    @Override
    public LoginResponse ssoLogin(SsoLoginRequest request) {
        log.info("Processing SSO login for provider: {}", request.provider());

        ssoConfigurationRepository.findByProviderTypeAndIsActiveTrue(request.provider())
                .orElseThrow(() -> new RuntimeException("SSO provider is temporarily unavailable. Please try again later."));

        // MOCK: Extract email from token. In reality, validate token with provider.
        String dummyEmail = request.ssoToken().contains("@") ? request.ssoToken() : "admin@company.com";
        
        User user = userRepository.findByEmail(dummyEmail)
                .orElseThrow(() -> new BadCredentialsException("SSO token is invalid or expired"));

        return buildLoginResponse(user);
    }

    @Override
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        String token = request.refreshToken();
        try {
            String email = jwtService.extractUsername(token);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new BadCredentialsException("Refresh token is invalid or expired"));

            if (jwtService.isTokenValid(token, user.getEmail())) {
                return buildLoginResponse(user);
            }
        } catch (Exception e) {
            log.warn("Invalid refresh token: {}", e.getMessage());
        }
        throw new BadCredentialsException("Refresh token is invalid or expired");
    }

    @Override
    public void logout(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);
            jwtService.invalidateToken(jwt);
            log.info("Token invalidated successfully");
        }
    }

    @Override
    public UserProfileResponse getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                Collections.singletonList("ROLE_" + user.getRole().name()),
                user.getStatus(),
                null // lastLoginAt is not persisted in MVP schema
        );
    }

    private LoginResponse buildLoginResponse(User user) {
        String accessToken = jwtService.generateToken(user.getEmail(), Map.of("role", user.getRole().name()));
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        return new LoginResponse(
                accessToken,
                refreshToken,
                (int) jwtService.getJwtExpiration(),
                "Bearer",
                user.getEmail(),
                user.getFullName(),
                Collections.singletonList("ROLE_" + user.getRole().name())
        );
    }
}
