package com.ats.auth.controller;

import com.ats.auth.dto.LoginRequest;
import com.ats.auth.dto.LoginResponse;
import com.ats.auth.dto.RefreshTokenRequest;
import com.ats.auth.dto.SsoLoginRequest;
import com.ats.auth.dto.UserProfileResponse;
import com.ats.auth.service.AuthService;
import com.ats.common.dto.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request : {}", request);

        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(BaseResponse.success("Login successful", response));
    }

    @PostMapping("/login/sso")
    public ResponseEntity<BaseResponse<LoginResponse>> ssoLogin(@Valid @RequestBody SsoLoginRequest request) {
        LoginResponse response = authService.ssoLogin(request);
        return ResponseEntity.ok(BaseResponse.success("SSO login successful", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse<LoginResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        LoginResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(BaseResponse.success("Token refreshed", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<Void>> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        authService.logout(token);
        return ResponseEntity.ok(BaseResponse.success("Logged out successfully", null));
    }

    @GetMapping("/me")
    public ResponseEntity<BaseResponse<UserProfileResponse>> getCurrentUser() {
        UserProfileResponse response = authService.getCurrentUser();
        return ResponseEntity.ok(BaseResponse.success("User profile retrieved", response));
    }
}
