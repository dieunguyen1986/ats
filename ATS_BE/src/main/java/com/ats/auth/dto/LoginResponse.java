package com.ats.auth.dto;

import java.util.List;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        Integer expiresIn,
        String tokenType,
        String email,
        String fullName,
        List<String> roles
) {
}
