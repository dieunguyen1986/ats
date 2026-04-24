package com.ats.auth.dto;

import com.ats.auth.entity.UserStatus;

import java.time.LocalDateTime;
import java.util.List;

public record UserProfileResponse(
        Long id,
        String email,
        String fullName,
        List<String> roles,
        UserStatus status,
        LocalDateTime lastLoginAt
) {
}
